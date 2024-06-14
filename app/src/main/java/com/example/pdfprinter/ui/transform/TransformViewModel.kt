package com.example.pdfprinter.ui.transform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pdfprinter.models.MCPPDFModel

class TransformViewModel: ViewModel() {

    private val pdfModelMutableLiveData = MutableLiveData<MCPPDFModel>()
    val pdfModelLiveData: LiveData<MCPPDFModel> get() = pdfModelMutableLiveData

    init {
        pdfModelMutableLiveData.value = MCPPDFModel.dummyMCPPDFModel
    }
}