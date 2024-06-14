package com.example.pdfprinter.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.PdfPrinter.databinding.FragmentMCPBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class MCPFragment: Fragment() {
    private lateinit var binding: FragmentMCPBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMCPBinding.inflate(inflater, container, false)
        return  binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.romLayout.title.text = "ROM"
        binding.romLayout.progressTv.text = "20`"
        binding.romLayout.progressBar.progress = 20

        drawMovementGraph()
        drawHistoryGraph()
    }


    private fun drawMovementGraph() {
        val lineEntries = getDataSet()
        val lineDataSet = LineDataSet(lineEntries, "Work")
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.isHighlightEnabled = true
        lineDataSet.lineWidth = 2f
        lineDataSet.color = Color.RED
        lineDataSet.setCircleColor(Color.YELLOW)
        lineDataSet.circleRadius = 6f
        lineDataSet.circleHoleRadius = 3f
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawHighlightIndicators(true)
        lineDataSet.highLightColor = Color.RED
        lineDataSet.valueTextSize = 12f
        lineDataSet.valueTextColor = Color.DKGRAY
        lineDataSet.mode = LineDataSet.Mode.STEPPED

        val lineData = LineData(lineDataSet)
        val lineChart = binding.movementChart
        lineChart.description.textSize = 12f
        lineChart.description.isEnabled = false
        lineChart.setDrawMarkers(false)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.animateY(1000)
        lineChart.xAxis.isGranularityEnabled = true
        lineChart.xAxis.granularity = 1.0f
        lineChart.data = lineData

        val xAxisLabel = ArrayList<String>()
        xAxisLabel.add("Rest")
        xAxisLabel.add("Work")
        xAxisLabel.add("2-up")

        val xAxis = lineChart.xAxis
        xAxis.axisMaximum = 3f
        xAxis.granularity = 1f
        xAxis.valueFormatter = object: IndexAxisValueFormatter(xAxisLabel) {
        }

        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 24f

        lineChart.axisRight.isEnabled = false

        lineChart.invalidate()
    }

    private fun drawHistoryGraph() {

        val lineEntries = getDataSet()
        val lineDataSet = LineDataSet(lineEntries, "Work")
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.isHighlightEnabled = true
        lineDataSet.lineWidth = 2f
        lineDataSet.color = Color.RED
        lineDataSet.setCircleColor(Color.YELLOW)
        lineDataSet.circleRadius = 6f
        lineDataSet.circleHoleRadius = 3f
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawHighlightIndicators(true)
        lineDataSet.highLightColor = Color.RED
        lineDataSet.valueTextSize = 12f
        lineDataSet.valueTextColor = Color.DKGRAY
        lineDataSet.mode = LineDataSet.Mode.STEPPED

        val lineData = LineData(lineDataSet)
        val lineChart = binding.historyChart
        lineChart.description.textSize = 12f
        lineChart.description.isEnabled = false
        lineChart.setDrawMarkers(false)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.animateY(1000)
        lineChart.xAxis.isGranularityEnabled = true
        lineChart.xAxis.granularity = 1.0f
        lineChart.data = lineData

        val xAxisLabel = ArrayList<String>()
        xAxisLabel.add("Rest")
        xAxisLabel.add("Work")
        xAxisLabel.add("2-up")

        val xAxis = lineChart.xAxis
        xAxis.axisMaximum = 3f
        xAxis.granularity = 1f
        xAxis.valueFormatter = object: IndexAxisValueFormatter(xAxisLabel) {
        }

        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 24f

        lineChart.axisRight.isEnabled = false

        lineChart.invalidate()

    }

    private fun getDataSet(): List<Entry> {
        val lineEntries: MutableList<Entry> = ArrayList()
        lineEntries.add(Entry(0f, 4f))
        lineEntries.add(Entry(1f, 3f))
        lineEntries.add(Entry(2f, 6f))
        lineEntries.add(Entry(3f, 8f))
        lineEntries.add(Entry(4f, 2f))
        lineEntries.add(Entry(5f, 3f))
        lineEntries.add(Entry(6f, 1f))
        return lineEntries
    }


    companion object {
        fun newInstance() = MCPFragment()
    }
}