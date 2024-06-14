package com.example.pdfprinter.ui.transform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.PdfPrinter.databinding.FragmentTransformBinding
import com.example.pdfprinter.ui.fragments.MCPFragment
import com.example.pdfprinter.ui.fragments.PIPFragment
import com.google.android.material.tabs.TabLayoutMediator

class TransformFragment: Fragment() {
    private lateinit var binding: FragmentTransformBinding
    private val transformViewModel: TransformViewModel by viewModels()
    private val tabItems = arrayListOf("MCP", "PIP")

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
                    1 -> PIPFragment.newInstance()
                    else -> MCPFragment.newInstance()
                }
            }

        }

        TabLayoutMediator(binding.tabLayout!!, binding.viewPager!!) { tab, position ->
            tab.text = tabItems[position]
        }.attach()

        return binding.root
    }

    companion object
}