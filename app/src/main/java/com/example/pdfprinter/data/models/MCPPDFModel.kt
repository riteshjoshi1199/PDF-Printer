package com.example.pdfprinter.data.models

import android.util.Log
import com.github.mikephil.charting.data.Entry
import com.google.firebase.database.DataSnapshot

data class MCPPDFModel(
    val isAssisted: Boolean,
    val sessionTime: Int,
    val movementScore: Int,
    val successRate: Int,
    val rom: Int,
    val movementChartData: List<MovementEntry>,
) {
    companion object {
        private const val TAG = "MCPPDFModel"

        fun create(dataSnapshot: DataSnapshot?): MCPPDFModel? {
            return if (dataSnapshot != null && dataSnapshot.exists()) {
                MCPPDFModel(
                    isAssisted = dataSnapshot.child("isAssisted").value.toString().toBoolean(),
                    sessionTime = dataSnapshot.child("sessionTime").value.toString().toInt(),
                    movementScore = dataSnapshot.child("movementScore").value.toString().toInt(),
                    successRate = dataSnapshot.child("successRate").value.toString().toInt(),
                    rom = dataSnapshot.child("rom").value.toString().toInt(),
                    movementChartData = dataSnapshot.child("movementData").value?.let {
                        val entries = ArrayList<MovementEntry>()
                        val listOfMap = it as? List<Map<String, Any?>>
                        Log.d(TAG, "create: listOfMap, $listOfMap")
                        listOfMap?.forEach { map ->
                            entries.add(
                                MovementEntry(
                                    map["timeInSecond"].toString().toInt(),
                                    map["angle"].toString().toInt()
                                )
                            )
                        }
                        entries
                    } ?: listOf()
                )
            } else {
                null
            }
        }
    }

}

data class MovementEntry(
    val timeInSecond: Int,
    val angle: Int,
): Entry(timeInSecond.toFloat(), angle.toFloat()) {
    companion object {
        fun create(dataSnapshot: DataSnapshot?): MovementEntry? {
            return null
        }
    }
}


data class HistoricalDataModel(
    val historicalData: List<HistoryEntry>,
) {
    companion object {
        private const val TAG = "HistoricalDataModel"
        fun create(dataSnapshot: DataSnapshot?): HistoricalDataModel? {
            return if (dataSnapshot != null && dataSnapshot.exists()) {
                val listOfMaps = dataSnapshot.value as? List<Map<String, Any?>>
                Log.d(TAG, "create: listOfMaps $listOfMaps")
                val entry = ArrayList<HistoryEntry>()
                listOfMaps?.forEachIndexed { index, map ->
                    entry.add(
                        HistoryEntry(
                            index,
                            map["rom"].toString().toInt(),
                            map["date"].toString()
                        ))
                }
                return HistoricalDataModel(
                    historicalData = entry
                )
            } else {
                null
            }
        }
    }
}


data class HistoryEntry(
    val entryNumber: Int,
    val rom: Int,
    val date: String,
): Entry(entryNumber.toFloat(), rom.toFloat())
