package com.example.pdfprinter.models

import com.github.mikephil.charting.data.Entry

data class MCPPDFModel(
    val seasonImageUrl: String,
    val seasonName: String,
    val isAssisted: Boolean,
    val sessionTime: Int,
    val movementScore: Int,
    val successRate: Int,
    val rom: Int,
    val movementChartData: List<MovementEntry>,
    val historyChartData: List<HistoryEntry>,
) {
    companion object {
        // Dummy data
        private val dummyMovementData = listOf(
            MovementEntry(0, -10),
            MovementEntry(1, 15),
            MovementEntry(2, 20),
            MovementEntry(3, 5),
            MovementEntry(4, 25),
            MovementEntry(5, -5),
            MovementEntry(6, 30),
            MovementEntry(7, -15),
            MovementEntry(8, 40),
            MovementEntry(9, 20)
        )

        private val dummyHistoryData = listOf(
            HistoryEntry(1, 20),
            HistoryEntry(2, 15),
            HistoryEntry(3, 30),
            HistoryEntry(4, 25),
            HistoryEntry(5, 40)
        )

        val dummyMCPPDFModel = MCPPDFModel(
            seasonImageUrl = "https://picsum.photos/200/300",
            seasonName = "Winter",
            isAssisted = false,
            sessionTime = 2,
            movementScore = 8,
            successRate = 88,
            rom = 20,
            movementChartData = dummyMovementData,
            historyChartData = dummyHistoryData
        )
    }
}

data class MovementEntry(
    val timeInSecond: Int,
    val angle: Int,
): Entry(timeInSecond.toFloat(), angle.toFloat())


data class HistoryEntry(
    val date: Int,
    val rom: Int,
): Entry(date.toFloat(), rom.toFloat())
