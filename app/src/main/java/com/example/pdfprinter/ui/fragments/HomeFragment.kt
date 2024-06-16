package com.example.pdfprinter.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.example.pdfprinter.R
import com.example.pdfprinter.data.models.MCPPDFModel
import com.example.pdfprinter.databinding.FragmentHomeBinding
import com.example.pdfprinter.databinding.LayoutSingleHvItemBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val tabItems = arrayListOf("MCP", "PIP")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewPager.adapter = object: FragmentStateAdapter(this@HomeFragment) {
            override fun getItemCount(): Int = tabItems.size

            override fun createFragment(position: Int): Fragment {
                return when(position){
                    0 -> MCPFragment.newInstance()
                    else -> MCPFragment.newInstance()
                }
            }

        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabItems[position]
        }.attach()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityViewModel.currentAppUserNameLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.welcomeText.text = "Hello $it, here is your performance."
            }
        }

        mainActivityViewModel.currentSessionNameLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.seasonNameTextview.text = it
            }
        }

        mainActivityViewModel.currentSessionImgUrlLiveData.observe(viewLifecycleOwner) {
            it?.let {
                Glide.with(binding.seasonImageView).load(it)
                    .fitCenter().circleCrop()
                    .into(binding.seasonImageView)
            }
        }

        val dateAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.date_array,
            android.R.layout.simple_spinner_item
        )

        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.dateSpinner.adapter = dateAdapter

        binding.dateSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                mainActivityViewModel.setCurrentDate(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        val sessionAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.session_array,
            android.R.layout.simple_spinner_item
        )


        sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sessionSpinner.adapter = sessionAdapter

        binding.sessionSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                mainActivityViewModel.setCurrentSession(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        getDataForCurrentDate()

        mainActivityViewModel.selectedSessionDataLiveData.observe(viewLifecycleOwner) {
            it?.let {
                setupPerformanceView(it)
            }
        }
    }

    private fun getDataForCurrentDate() {
        //let assume current date is day1
        binding.dateSpinner.setSelection(0)
        binding.sessionSpinner.setSelection(0)
    }


    private fun setupPerformanceView(mcpPDFModel: MCPPDFModel) {
        Log.d(TAG, "setupPerformanceView: $mcpPDFModel")

        binding.assistedStatus!!.text = if (mcpPDFModel.isAssisted) "YES" else "NO"

        binding.performanceLl.removeAllViews()
        val sessionTimeItemBinding = LayoutSingleHvItemBinding.inflate(layoutInflater, binding.performanceLl, false)
        sessionTimeItemBinding.title.text = "Session Time"
        val sessionInPercent = (mcpPDFModel.sessionTime / 2.0) * 100.0
        sessionTimeItemBinding.progressBar.progress = if (sessionInPercent >= 100) 100 else sessionInPercent.toInt()
        sessionTimeItemBinding.progressTv.text = mcpPDFModel.sessionTime.toString()

        binding.performanceLl.addView(sessionTimeItemBinding.root)


        val movementScoreItemBinding = LayoutSingleHvItemBinding.inflate(layoutInflater, binding.performanceLl, false)
        movementScoreItemBinding.title.text = "Movement Score"
        val movementScoreInPercent = (mcpPDFModel.movementScore / 8.0) * 100.0
        movementScoreItemBinding.progressBar.progress = if (movementScoreInPercent >= 100) 100 else movementScoreInPercent.toInt()
        movementScoreItemBinding.progressTv.text = mcpPDFModel.movementScore.toString()

        binding.performanceLl.addView(movementScoreItemBinding.root)


        val successRateItemBinding = LayoutSingleHvItemBinding.inflate(layoutInflater, binding.performanceLl, false)
        successRateItemBinding.title.text = "Success Rate"
        val successRateInPercent = mcpPDFModel.successRate * 1.0
        successRateItemBinding.progressBar.progress = if (successRateInPercent >= 100) 100 else successRateInPercent.toInt()
        successRateItemBinding.progressTv.text = mcpPDFModel.successRate.toString()

        binding.performanceLl.addView(successRateItemBinding.root)

    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}