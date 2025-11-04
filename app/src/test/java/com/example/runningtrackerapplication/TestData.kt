package com.example.runningtrackerapplication

object TestData {

    fun createSampleRun(): Run {
        return Run(
            id = "test_id_1",
            name = "Test Run",
            description = "Test Description",
            distanceInKm = 5.0,
            timeInMillis = 1800000, // 30 minutes
            timestamp = System.currentTimeMillis(),
            routePoints = createSampleRoutePoints()
        )
    }

    fun createSampleRoutePoints(): List<Map<String, Double>> {
        return listOf(
            mapOf("lat" to 42.6977, "lng" to 23.3219),
            mapOf("lat" to 42.6978, "lng" to 23.3220),
            mapOf("lat" to 42.6979, "lng" to 23.3221)
        )
    }

    fun createRunWithZeroDistance(): Run {
        return Run(
            id = "test_id_2",
            name = "Zero Distance Run",
            description = "No distance covered",
            distanceInKm = 0.0,
            timeInMillis = 600000, // 10 minutes
            timestamp = System.currentTimeMillis(),
            routePoints = emptyList()
        )
    }

    fun createRunWithLongTime(): Run {
        return Run(
            id = "test_id_3",
            name = "Long Run",
            description = "Half marathon training",
            distanceInKm = 21.1,
            timeInMillis = 7200000, // 2 hours
            timestamp = System.currentTimeMillis(),
            routePoints = createSampleRoutePoints()
        )
    }
}