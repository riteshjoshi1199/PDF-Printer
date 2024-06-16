package com.example.pdfprinter.ui.fragments

import androidx.lifecycle.ViewModel
import com.example.pdfprinter.data.repos.UserDataRepository

class MainActivityViewModel: ViewModel() {

    val currentAppUserNameLiveData get() = UserDataRepository.currentAppUserNameLiveData

    val currentSessionNameLiveData get() = UserDataRepository.currentSessionNameLiveData
    val currentSessionImgUrlLiveData get() = UserDataRepository.currentSessionImgUrlLiveData

    private var currentSelectedDate: String? = null
        private set(value) {
            if (field == value)
                return
            field = value

        }
    private var currentSelectedSession: String? = null
        private set(value) {
            if (field == value)
                return
            field = value

        }


    fun setCurrentDate(date: String) {
        currentSelectedDate = date
    }

    fun setCurrentSession(session: String) {
        currentSelectedSession = session
    }

    fun getDataForThiSession() {
        if (currentSelectedDate == null || currentSelectedSession == null) {
            throw Exception("You must set date and session")
        } else {
            UserDataRepository.getThisSessionDataForThisData(currentSelectedDate!!, currentSelectedSession!!)
        }
    }


    val selectedSessionDataLiveData get() = UserDataRepository.selectedSessionDataLiveData
    val historicalDataLiveData get() = UserDataRepository.historicalDataLiveData

    init {

        //initialize for first day
        setCurrentDate("day1")
        setCurrentSession("session1")
        UserDataRepository.getThisSessionDataForThisData(currentSelectedDate!!, currentSelectedSession!!)
    }
}