package com.example.pdfprinter.ui.transform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.PdfPrinter.databinding.FragmentTransformBinding
import com.example.PdfPrinter.databinding.LayoutSingleHvItemBinding
import com.example.pdfprinter.ui.fragments.MCPFragment
import com.google.android.material.tabs.TabLayoutMediator

class TransformFragment: Fragment() {
    private lateinit var binding: FragmentTransformBinding
    private val transformViewModel: TransformViewModel by viewModels()
    private val tabItems = arrayListOf("MCP")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransformBinding.inflate(inflater, container, false)
        binding.viewPager!!.adapter = object : FragmentStateAdapter(this@TransformFragment){
            override fun getItemCount(): Int = tabItems.size

            override fun createFragment(position: Int): Fragment {
                return when(position){
                    0 -> MCPFragment.newInstance()
                    else -> MCPFragment.newInstance()
                }
            }

        }

        TabLayoutMediator(binding.tabLayout!!, binding.viewPager!!) { tab, position ->
            tab.text = tabItems[position]
        }.attach()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.performanceLl!!.removeAllViews()
        val sessionTimeItemBinding = LayoutSingleHvItemBinding.inflate(layoutInflater, binding.performanceLl, false)
        sessionTimeItemBinding.title.text = "Session Time"
        sessionTimeItemBinding.progressBar.progress = 100
        sessionTimeItemBinding.progressTv.text = "2"

        binding.performanceLl!!.addView(sessionTimeItemBinding.root)


        val movementScoreItemBinding = LayoutSingleHvItemBinding.inflate(layoutInflater, binding.performanceLl, false)
        movementScoreItemBinding.title.text = "Movement Score"
        movementScoreItemBinding.progressBar.progress = 100
        movementScoreItemBinding.progressTv.text = "8"

        binding.performanceLl!!.addView(movementScoreItemBinding.root)


        val successRateItemBinding = LayoutSingleHvItemBinding.inflate(layoutInflater, binding.performanceLl, false)
        successRateItemBinding.title.text = "Success Rate"
        successRateItemBinding.progressBar.progress = 88
        successRateItemBinding.progressTv.text = "88"

        binding.performanceLl!!.addView(successRateItemBinding.root)


    }

    companion object
}