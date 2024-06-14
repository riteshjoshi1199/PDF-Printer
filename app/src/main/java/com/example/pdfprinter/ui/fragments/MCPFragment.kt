package com.example.pdfprinter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.PdfPrinter.databinding.FragmentMCPBinding
import com.example.pdfprinter.databinding.FragmentMCPBinding

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
    }


    companion object {
        fun newInstance() = MCPFragment()
    }
}