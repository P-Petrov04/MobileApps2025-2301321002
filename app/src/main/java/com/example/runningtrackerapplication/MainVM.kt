package com.example.runningtrackerapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = RunRepository()

    // Състояние за списъка с всички пробези - сега с LiveData
    private val _runs = MutableLiveData<List<Run>>(emptyList())
    val runs: LiveData<List<Run>> = _runs

    // Функция за стартиране на слушане за промени в данните
    init {
        listenToRuns()
    }

    private fun listenToRuns() {
        repository.getRuns().addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Има грешка, обработката й е тук
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
                // Актуализирай LiveData с новия списък
                _runs.value = runsList
            }
        }
    }

    // Функции за CRUD операции (остават същите)
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