package com.example.pdfprinter.ui.transform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pdfprinter.databinding.FragmentTransformBinding
import com.example.pdfprinter.ui.fragments.MCPFragment
import com.example.pdfprinter.ui.fragments.PIPFragment

class TransformFragment: Fragment() {
    private lateinit var binding: FragmentTransformBinding
    private val transformViewModel: TransformViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransformBinding.inflate(inflater, container, false)
        binding.viewPager!!.adapter = object : FragmentStateAdapter(this@TransformFragment){
            override fun getItemCount(): Int  = 2

            override fun createFragment(position: Int): Fragment {
                return when(position){
                    0 -> MCPFragment.newInstance()
                    1 -> PIPFragment.newInstance()
                    else -> MCPFragment.newInstance()
                }
            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance() = TransformFragment()
    }
}