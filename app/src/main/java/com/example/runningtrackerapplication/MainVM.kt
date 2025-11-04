package com.example.runningtrackerapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: RunRepository = RunRepository()
) : ViewModel() {

    private val _runs = MutableLiveData<List<Run>>(emptyList())
    val runs: LiveData<List<Run>> = _runs

    init {
        listenToRuns()
    }

    private fun listenToRuns() {
        repository.getRuns().addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            snapshot?.let { querySnapshot ->
                val runsList = mutableListOf<Run>()
                for (document in querySnapshot.documents) {
                    val run = document.toObject(Run::class.java)
                    run?.let {
                        runsList.add(it.copy(id = document.id))
                    }
                }
                _runs.value = runsList
            }
        }
    }

    fun addRun(run: Run) {
        viewModelScope.launch {
            repository.addRun(run)
        }
    }

    fun updateRun(run: Run) {
        viewModelScope.launch {
            repository.updateRun(run)
        }
    }

    fun deleteRun(runId: String) {
        viewModelScope.launch {
            repository.deleteRun(runId)
        }
    }
}