package com.example.runningtrackerapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: RunAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация на ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Инициализация на UI компоненти
        initViews()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.runsRecyclerView)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
    }

    private fun setupRecyclerView() {
        adapter = RunAdapter(emptyList()) { run ->
            // 👈 ПРОМЕНИ ТОВА - отваряне на EditRunActivity
            val intent = Intent(this, EditRunActivity::class.java).apply {
                putExtra("RUN_EXTRA", run)
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.runs.observe(this) { runs ->
            adapter.updateRuns(runs)
        }
    }

    private fun setupClickListeners() {
        startButton.setOnClickListener {
            // Временно - тестов запис
            viewModel.addRun(
                Run(
                    name = "Тестов пробег",
                    distanceInKm = 5.2,
                    timeInMillis = 1800000 // 30 минути
                )
            )
        }

        stopButton.setOnClickListener {
            // Ще имплементираме по-късно
        }
    }
}