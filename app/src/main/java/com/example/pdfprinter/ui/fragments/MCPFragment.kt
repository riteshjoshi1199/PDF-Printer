package com.example.pdfprinter.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.pdfprinter.data.models.HistoricalDataModel
import com.example.pdfprinter.data.models.HistoryEntry
import com.example.pdfprinter.data.models.MCPPDFModel
import com.example.pdfprinter.data.models.MovementEntry
import com.example.pdfprinter.databinding.FragmentMCPBinding
import com.example.pdfprinter.databinding.LayoutPdfBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.time.Duration.Companion.seconds


class MCPFragment: Fragment() {
    private lateinit var binding: FragmentMCPBinding
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMCPBinding.inflate(inflater, container, false)
        return  binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivityViewModel.selectedSessionDataLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.romLayout.title.text = "ROM"
                binding.romLayout.progressTv.text = it.rom.toString() + "`"
                val romInPercent = (it.rom / 100.0) * 100.0
                binding.romLayout.progressBar.progress = if (romInPercent >= 100) 100 else romInPercent.toInt()
                drawMovementGraph(binding.movementChart, it.movementChartData)
            }
        }

        mainActivityViewModel.historicalDataLiveData.observe(viewLifecycleOwner) {
            it?.let {
                drawHistoryGraph(binding.historyChart, it.historicalData)
            }
        }

        binding.fab.setOnClickListener {
            if (hasStoragePermission(requireContext())) {
                fetchDataForPdf()
            } else {
                askStoragePermission()
            }
        }
    }

    private fun fetchDataForPdf() {
        // we should use observer, I am using directly because it is already observed
        // and has value because of activityViewModels
        val selectedSessionData = mainActivityViewModel.selectedSessionDataLiveData.value
        val historicalData = mainActivityViewModel.historicalDataLiveData.value
        if (selectedSessionData == null || historicalData == null)
            return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.fab.visibility = View.GONE
                (Pair(selectedSessionData, historicalData)).let { it ->
                    Log.d(TAG, "fetchDataForPdf: $it")
                    Toast.makeText(requireContext(), "Please wait, creating PDF file", Toast.LENGTH_SHORT).show()
                    createAndExportPdf(it.first, it.second)?.let { pdfFile ->
                        Log.d(TAG, "fetchDataForPdf: $pdfFile")
                        delay(2.seconds)
                        Snackbar.make(binding.root, "Pdf Created", Snackbar.LENGTH_LONG)
                            .setAction("View") {
                                try {
                                    val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", pdfFile)
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setDataAndType(uri, "application/pdf")
                                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                                    val chooser = Intent.createChooser(intent, "Open PDF File")
                                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(requireContext(), "No PDF Viewer Found", Toast.LENGTH_SHORT).show()
                                }
                            }.show()

                        binding.fab.visibility = View.VISIBLE
                    } ?: run {
                        Log.d(TAG, "fetchDataForPdf: file not created")
                    }
                }
            } catch (error: Exception) {
                Log.d(TAG, "fetchDataForPdf: ", error)
            }
        }
    }

    private fun createAndExportPdf(model: MCPPDFModel, historicalData: HistoricalDataModel): File? {
        setContentToPdf(model, historicalData).let { pdfView ->
            pdfView.measure(
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.EXACTLY)
            )
//            pdfView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            pdfView.layout(0, 0, pdfView.measuredWidth, pdfView.measuredHeight)

            val mcpPdfFile = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(pdfView.measuredWidth, pdfView.measuredHeight, 1).create()
            val page = mcpPdfFile.startPage(pageInfo)
            pdfView.draw(page.canvas)
            mcpPdfFile.finishPage(page)

            try {
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                var filePath = File(downloadDir, "MCP_${System.currentTimeMillis()}.pdf")
                if (filePath.exists()) {
                    filePath = File(downloadDir, "MCP_${System.currentTimeMillis()}_${Math.random() * 100}.pdf")
                }

                val fos = FileOutputStream(filePath)
                mcpPdfFile.writeTo(fos)
                mcpPdfFile.close()
                fos.close()
                return filePath
            } catch (e: Exception) {
                Log.e(TAG, "createAndExportPdf: ", e)
                return null
            }
        }
    }

    private fun setContentToPdf(model: MCPPDFModel, historicalDataModel: HistoricalDataModel): View {
        val pdfViewBinding = LayoutPdfBinding.inflate(layoutInflater, null, false)
        pdfViewBinding.romLayout.title.text = "ROM"
        pdfViewBinding.romLayout.progressTv.text = model.rom.toString() + "`"
        val romPercent = (model.rom / 100.0) * 100
        pdfViewBinding.romLayout.progressBar.animation = null
        pdfViewBinding.romLayout.progressBar.progress = if (romPercent >= 100) 100 else romPercent.toInt()

        drawMovementGraph(pdfViewBinding.movementChart, model.movementChartData)
        pdfViewBinding.movementChart.invalidate()
        drawHistoryGraph(pdfViewBinding.historyChart, historicalDataModel.historicalData)
        pdfViewBinding.historyChart.invalidate()

        return pdfViewBinding.root
    }

    private val requestStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val isGranted = permissions.entries.all { it.value }
        if (!isGranted) {
            var shouldShowRequestPermissionRationale = false
            permissions.entries.forEach { p ->
                if (shouldShowRequestPermissionRationale(p.toString())) {
                    shouldShowRequestPermissionRationale = true
                }
            }

            if (shouldShowRequestPermissionRationale) {
                showStoragePermissionRationale()
            } else {
                Toast.makeText(requireContext(), "Permission Denied, Please enable permission from settings", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showStoragePermissionRationale() {
        MaterialAlertDialogBuilder(requireContext()).setTitle("Permission Required")
            .setMessage("Storage permission is required to save your data")
            .setPositiveButton("Ok") { _, _ ->
                requestStoragePermissionLauncher.launch(STORAGE_PERMISSION)
            }.setNegativeButton("Cancel", null)
            .show()
    }

    private fun askStoragePermission() {
        requestStoragePermissionLauncher.launch(STORAGE_PERMISSION)
    }

    private fun hasStoragePermission(context: Context): Boolean {
        return STORAGE_PERMISSION.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun drawMovementGraph(lineChart: LineChart, movementChartData: List<MovementEntry>) {
        Log.d(TAG, "drawMovementGraph: $movementChartData")

        val lineDataSet = LineDataSet(movementChartData, "Time(s)")

        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.isHighlightEnabled = true
        lineDataSet.lineWidth = 2f
        lineDataSet.color = Color.BLUE
        lineDataSet.setCircleColor(Color.YELLOW)
        lineDataSet.circleRadius = 6f
        lineDataSet.circleHoleRadius = 3f
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawHighlightIndicators(true)
        lineDataSet.highLightColor = Color.BLUE
        lineDataSet.valueTextSize = 12f
        lineDataSet.valueTextColor = Color.DKGRAY
        lineDataSet.mode = LineDataSet.Mode.STEPPED

        val lineData = LineData(lineDataSet)
        lineChart.description.textSize = 12f
        lineChart.description.isEnabled = false
        lineChart.setDrawMarkers(false)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//        lineChart.animateY(1000)
        lineChart.xAxis.isGranularityEnabled = true
        lineChart.xAxis.granularity = 1.0f
        lineChart.data = lineData

        val xAxisLabel = ArrayList<String>()
        xAxisLabel.add("Time")
        xAxisLabel.addAll(movementChartData.map { it.timeInSecond.toString() })

        val xAxis = lineChart.xAxis
        xAxis.axisMaximum = 7f
        xAxis.granularity = 1f
        xAxis.valueFormatter = object: IndexAxisValueFormatter(xAxisLabel) {
            override fun getFormattedValue(value: Float): String {
                return (value.toInt() * 200).toString()
            }
        }


        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = -20f
        yAxis.axisMaximum = 60f

        lineChart.axisRight.isEnabled = false

        lineChart.invalidate()
    }

    private fun drawHistoryGraph(lineChart: LineChart, historyChartData: List<HistoryEntry>) {
        Log.d(TAG, "drawHistoryGraph: $historyChartData")

        val lineDataSet = LineDataSet(historyChartData, "Date")
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.isHighlightEnabled = true
        lineDataSet.lineWidth = 2f
        lineDataSet.color = Color.BLUE
        lineDataSet.setCircleColor(Color.YELLOW)
        lineDataSet.circleRadius = 6f
        lineDataSet.circleHoleRadius = 3f
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawHighlightIndicators(true)
        lineDataSet.highLightColor = Color.BLUE
        lineDataSet.valueTextSize = 12f
        lineDataSet.valueTextColor = Color.DKGRAY
        lineDataSet.mode = LineDataSet.Mode.STEPPED

        val lineData = LineData(lineDataSet)
        lineChart.description.textSize = 12f
        lineChart.description.isEnabled = false
        lineChart.setDrawMarkers(false)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//        lineChart.animateY(1000)
        lineChart.xAxis.isGranularityEnabled = true
        lineChart.xAxis.granularity = 1.0f
        lineChart.data = lineData

        val xAxisLabel = ArrayList<String>()
        xAxisLabel.addAll(historyChartData.map { it.date })

        val xAxis = lineChart.xAxis
        xAxis.axisMaximum = 5f
        xAxis.granularity = 1f
        xAxis.valueFormatter = object: IndexAxisValueFormatter(xAxisLabel) {

        }


        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 50f

        lineChart.axisRight.isEnabled = false

        lineChart.invalidate()
    }


    companion object {
        fun newInstance() = MCPFragment()

        private const val TAG = "MCPFragment"

        private val STORAGE_PERMISSION: Array<String> = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            arrayOf()
        }
    }
}