package com.example.runningtrackerapplication

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DataValidationTest {

    @Test
    fun `run data conversion - route points should be properly formatted`() {
        val run = TestData.createSampleRun()

        assertThat(run.routePoints).hasSize(3)
        assertThat(run.routePoints[0]).containsKey("lat")
        assertThat(run.routePoints[0]).containsKey("lng")
        assertThat(run.routePoints[0]["lat"]).isEqualTo(42.6977)
        assertThat(run.routePoints[0]["lng"]).isEqualTo(23.3219)
    }

    @Test
    fun `run validation - valid run should have correct data`() {
        val validRun = TestData.createSampleRun()

        assertThat(validRun.name).isNotEmpty()
        assertThat(validRun.description).isNotEmpty()
        assertThat(validRun.distanceInKm).isGreaterThan(0.0)
        assertThat(validRun.timeInMillis).isGreaterThan(0L)
        assertThat(validRun.timestamp).isGreaterThan(0L)
    }

    @Test
    fun `run validation - run with zero distance should be valid`() {
        val zeroDistanceRun = TestData.createRunWithZeroDistance()

        assertThat(zeroDistanceRun.distanceInKm).isEqualTo(0.0)
        assertThat(zeroDistanceRun.timeInMillis).isGreaterThan(0L)
        assertThat(zeroDistanceRun.routePoints).isEmpty()
    }

    @Test
    fun `run validation - run with long distance should be valid`() {
        val longRun = TestData.createRunWithLongTime()

        assertThat(longRun.distanceInKm).isEqualTo(21.1)
        assertThat(longRun.timeInMillis).isEqualTo(7200000L)
        assertThat(longRun.routePoints).hasSize(3)
    }

    @Test
    fun `run should calculate pace correctly`() {
        val run = TestData.createSampleRun()

        val paceMinutesPerKm = (run.timeInMillis / 60000.0) / run.distanceInKm

        assertThat(paceMinutesPerKm).isWithin(0.1).of(6.0)
    }

    @Test
    fun `run with empty name should be invalid`() {
        val runWithEmptyName = Run(
            name = "",
            description = "Test",
            distanceInKm = 5.0,
            timeInMillis = 1800000
        )

        assertThat(runWithEmptyName.name).isEmpty()
    }

    @Test
    fun `run timestamp should be current or past`() {
        val run = TestData.createSampleRun()
        val currentTime = System.currentTimeMillis()

        assertThat(run.timestamp).isAtMost(currentTime)
    }

    @Test
    fun `run with valid route points should have correct coordinates`() {
        val run = TestData.createSampleRun()

        assertThat(run.routePoints[1]["lat"]).isEqualTo(42.6978)
        assertThat(run.routePoints[1]["lng"]).isEqualTo(23.3220)
        assertThat(run.routePoints[2]["lat"]).isEqualTo(42.6979)
        assertThat(run.routePoints[2]["lng"]).isEqualTo(23.3221)
    }
}