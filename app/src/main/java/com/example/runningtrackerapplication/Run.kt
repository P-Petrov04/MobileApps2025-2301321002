package com.example.runningtrackerapplication

import java.io.Serializable

data class Run(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val distanceInKm: Double = 0.0,
    val timeInMillis: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val routePoints: List<Map<String, Double>> = emptyList()
) : Serializable {
    constructor() : this("", "", "", 0.0, 0, 0, emptyList())
}