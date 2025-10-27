package com.example.runningtrackerapplication

import java.io.Serializable

// 👈 ДОБАВИ "Serializable"
data class Run(
    val id: String = "",
    val name: String = "",
    val distanceInKm: Double = 0.0,
    val timeInMillis: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable { // 👈 ДОБАВИ ТОВА
    constructor() : this("", "", 0.0, 0, 0)
}