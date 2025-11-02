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
    private lateinit var startNewRunButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initViews()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.runsRecyclerView)
        startNewRunButton = findViewById(R.id.startNewRunButton)
    }

    private fun setupRecyclerView() {
        adapter = RunAdapter(emptyList()) { run ->
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
        startNewRunButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}