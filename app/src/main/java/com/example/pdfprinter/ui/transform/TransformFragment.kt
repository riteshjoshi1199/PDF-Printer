package com.example.pdfprinter.ui.transform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.example.PdfPrinter.databinding.FragmentTransformBinding
import com.example.PdfPrinter.databinding.LayoutSingleHvItemBinding
import com.example.pdfprinter.models.MCPPDFModel
import com.example.pdfprinter.ui.fragments.MCPFragment
import com.google.android.material.tabs.TabLayoutMediator

class TransformFragment: Fragment() {
    private lateinit var binding: FragmentTransformBinding
    private val transformViewModel: TransformViewModel by activityViewModels()
    private val tabItems = arrayListOf("MCP")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransformBinding.inflate(inflater, container, false)
        binding.viewPager.adapter = object: FragmentStateAdapter(this@TransformFragment) {
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
        transformViewModel.pdfModelLiveData.observe(viewLifecycleOwner) {
            setupPerformanceView(it)
        }
    }

    private fun setupPerformanceView(mcpPDFModel: MCPPDFModel) {
        binding.seasonNameTextview.text = mcpPDFModel.seasonName
        Glide.with(binding.seasonImageView).load(mcpPDFModel.seasonImageUrl).fitCenter().circleCrop()
            .into(binding.seasonImageView)

        binding.performanceLl.removeAllViews()
        val sessionTimeItemBinding = LayoutSingleHvItemBinding.inflate(layoutInflater, binding.performanceLl, false)
        sessionTimeItemBinding.title.text = "Session Time"
        val sessionInPercent = (mcpPDFModel.sessionTime / 2) * 100
        sessionTimeItemBinding.progressBar.progress = if (sessionInPercent >= 100) 100 else sessionInPercent
        sessionTimeItemBinding.progressTv.text = mcpPDFModel.sessionTime.toString()

        binding.performanceLl.addView(sessionTimeItemBinding.root)


        val movementScoreItemBinding = LayoutSingleHvItemBinding.inflate(layoutInflater, binding.performanceLl, false)
        movementScoreItemBinding.title.text = "Movement Score"
        val movementScoreInPercent = (mcpPDFModel.movementScore / 8) * 100
        movementScoreItemBinding.progressBar.progress = if (movementScoreInPercent >= 100) 100 else movementScoreInPercent.toInt()
        movementScoreItemBinding.progressTv.text = mcpPDFModel.movementScore.toString()

        binding.performanceLl.addView(movementScoreItemBinding.root)


        val successRateItemBinding = LayoutSingleHvItemBinding.inflate(layoutInflater, binding.performanceLl, false)
        successRateItemBinding.title.text = "Success Rate"
        val successRateInPercent = mcpPDFModel.successRate
        successRateItemBinding.progressBar.progress = if (successRateInPercent >= 100) 100 else successRateInPercent
        successRateItemBinding.progressTv.text = mcpPDFModel.successRate.toString()

        binding.performanceLl.addView(successRateItemBinding.root)

    }

    companion object
}