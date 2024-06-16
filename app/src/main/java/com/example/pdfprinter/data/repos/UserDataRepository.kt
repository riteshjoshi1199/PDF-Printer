package com.example.pdfprinter.data.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pdfprinter.data.models.HistoricalDataModel
import com.example.pdfprinter.data.models.MCPPDFModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

object UserDataRepository {
    private const val currentAppUserKey: String = "user1"
    private val firebaseDatabase = Firebase.database
    private val databaseReference = firebaseDatabase.reference
    private var currentUserName: String? = null
    private var currentUserAge: Int? = null
    private var currentSeasonImgUrl: String? = null
    private var currentSeasonName: String? = null

    private val currentAppUserNameMutableLiveData: MutableLiveData<String?> = MutableLiveData<String?>(currentSeasonName)
    val currentAppUserNameLiveData: LiveData<String?> get() = currentAppUserNameMutableLiveData

    private val currentSessionImgUrlMutableLiveData: MutableLiveData<String?> = MutableLiveData<String?>(currentSeasonImgUrl)
    val currentSessionImgUrlLiveData: LiveData<String?> get() = currentSessionImgUrlMutableLiveData

    private val currentSessionNameMutableLiveData: MutableLiveData<String?> = MutableLiveData<String?>(currentSeasonName)
    val currentSessionNameLiveData: LiveData<String?> get() = currentSessionNameMutableLiveData

    private val currentUserAgeMutableLiveData: MutableLiveData<Int?> = MutableLiveData<Int?>(currentUserAge)
    val currentUserAgeLiveData: LiveData<Int?> get() = currentUserAgeMutableLiveData


    private fun getCurrentUserData() {
        Log.d(TAG, "getCurrentUserData: ")
        databaseReference.child("user").child(currentAppUserKey).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: ${snapshot.value}")
                currentUserName = snapshot.child("userName").value as? String
                currentAppUserNameMutableLiveData.value = currentUserName

                currentSeasonImgUrl = snapshot.child("currentSeasonImgUrl").value as? String
                currentSessionImgUrlMutableLiveData.value = currentSeasonImgUrl

                currentSeasonName = snapshot.child("currentSeasonName").value as? String
                currentSessionNameMutableLiveData.value = currentSeasonName

                currentUserAge = snapshot.child("useAge").value as? Int
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read data ${error.message}")
            }
        })
    }

    private val selectedSessionDataMutableLiveData: MutableLiveData<MCPPDFModel?> = MutableLiveData<MCPPDFModel?>()
    val selectedSessionDataLiveData: LiveData<MCPPDFModel?> get() = selectedSessionDataMutableLiveData

    fun getThisSessionDataForThisData(sessionDate: String, sessionName: String) {
        Log.d(TAG, "getThisSessionDataForThisData: $sessionDate, $sessionName")
        databaseReference.child("userWiseDayWiseSessionData").child(currentAppUserKey)
            .child(sessionDate).child(sessionName).get().addOnSuccessListener {
                Log.d(TAG, "getThisSessionDataForThisData: ${it.value}")
                val data = MCPPDFModel.create(it)
                selectedSessionDataMutableLiveData.value = data
            }
    }


    private val historicalDataMutableLiveData: MutableLiveData<HistoricalDataModel?> = MutableLiveData()
    val historicalDataLiveData: LiveData<HistoricalDataModel?> get() = historicalDataMutableLiveData

    private fun getCurrentUserHistoricalData() {
        Log.d(TAG, "getCurrentUserHistoricalData: ")
        databaseReference.child("userWiseDayWiseData").child(currentAppUserKey).addValueEventListener(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG, "onDataChange: ${snapshot.value}")
                    val data = HistoricalDataModel.create(snapshot)
                    historicalDataMutableLiveData.value = data
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to read data ${error.message}")
                }
            }
        )
    }

    init {
        getCurrentUserData()
        getCurrentUserHistoricalData()
    }

    const val TAG = "UserDataRepository"

}