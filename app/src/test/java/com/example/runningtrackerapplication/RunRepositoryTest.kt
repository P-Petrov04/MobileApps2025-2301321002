package com.example.runningtrackerapplication

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RunRepositoryTest {

    @Test
    fun `run data conversion should work correctly`() {

        val run = TestData.createSampleRun()


        assertThat(run.routePoints).hasSize(3)
        assertThat(run.routePoints[0]["lat"]).isEqualTo(42.6977)
        assertThat(run.routePoints[0]["lng"]).isEqualTo(23.3219)
    }

    @Test
    fun `run validation should work for zero distance`() {

        val run = TestData.createRunWithZeroDistance()


        assertThat(run.distanceInKm).isEqualTo(0.0)
        assertThat(run.routePoints).isEmpty()
    }

    @Test
    fun `run validation should work for long runs`() {

        val run = TestData.createRunWithLongTime()

        assertThat(run.distanceInKm).isEqualTo(21.1)
        assertThat(run.timeInMillis).isEqualTo(7200000L)
    }

    @Test
    fun `run should have valid timestamp`() {
        val run = TestData.createSampleRun()

        assertThat(run.timestamp).isGreaterThan(0L)
    }

    @Test
    fun `run should have valid name and description`() {
        val run = TestData.createSampleRun()

        assertThat(run.name).isEqualTo("Test Run")
        assertThat(run.description).isEqualTo("Test Description")
    }
}