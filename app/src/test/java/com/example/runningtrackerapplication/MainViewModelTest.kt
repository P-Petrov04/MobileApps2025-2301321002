package com.example.runningtrackerapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Mock
    private lateinit var mockRepository: RunRepository

    @Mock
    private lateinit var mockQuery: com.google.firebase.firestore.Query

    @Mock
    private lateinit var mockRunsObserver: Observer<List<Run>>

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        `when`(mockRepository.getRuns()).thenReturn(mockQuery)

        viewModel = MainViewModel(mockRepository)
        viewModel.runs.observeForever(mockRunsObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addRun should call repository addRun`() = testScope.runTest {
        val run = TestData.createSampleRun()

        viewModel.addRun(run)

        advanceUntilIdle()

        verify(mockRepository).addRun(run)
    }

    @Test
    fun `updateRun should call repository updateRun`() = testScope.runTest {
        val run = TestData.createSampleRun()

        viewModel.updateRun(run)
        advanceUntilIdle()

        verify(mockRepository).updateRun(run)
    }

    @Test
    fun `deleteRun should call repository deleteRun`() = testScope.runTest {
        val runId = "test_run_id"

        viewModel.deleteRun(runId)
        advanceUntilIdle()

        verify(mockRepository).deleteRun(runId)
    }

    @Test
    fun `runs LiveData should be initialized as empty`() {
        val runs = viewModel.runs.value

        assertThat(runs).isNotNull()
        assertThat(runs).isEmpty()
    }
}