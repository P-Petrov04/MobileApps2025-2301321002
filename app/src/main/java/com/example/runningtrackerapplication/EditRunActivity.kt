package com.example.runningtrackerapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditRunActivity : AppCompatActivity() {

    private lateinit var editName: TextInputEditText
    private lateinit var editDistance: TextInputEditText
    private lateinit var editTime: TextInputEditText
    private lateinit var timestampText: TextView
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private lateinit var currentRun: Run
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_run)

        // Инициализация на ViewModel
        viewModel = MainViewModel()

        // Вземи предадения Run обект от intent
        currentRun = intent.getSerializableExtra("RUN_EXTRA") as? Run ?: run {
            Toast.makeText(this, "Грешка при зареждане на данните", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        populateData()
        setupClickListeners()
    }

    private fun initViews() {
        editName = findViewById(R.id.editName)
        editDistance = findViewById(R.id.editDistance)
        editTime = findViewById(R.id.editTime)
        timestampText = findViewById(R.id.timestampText)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)
    }

    private fun populateData() {
        // Попълни полетата с текущите данни
        editName.setText(currentRun.name)
        editDistance.setText(currentRun.distanceInKm.toString())
        editTime.setText((currentRun.timeInMillis / 60000).toString()) // Конвертирай в минути

        // Покажи датата на създаване
        val date = Date(currentRun.timestamp)
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        timestampText.text = "Създаден на: ${formatter.format(date)}"
    }

    private fun setupClickListeners() {
        saveButton.setOnClickListener {
            saveRun()
        }

        deleteButton.setOnClickListener {
            deleteRun()
        }
    }

    private fun saveRun() {
        // Валидация на данните
        val name = editName.text.toString().trim()
        val distanceText = editDistance.text.toString().trim()
        val timeText = editTime.text.toString().trim()

        if (name.isEmpty() || distanceText.isEmpty() || timeText.isEmpty()) {
            Toast.makeText(this, "Моля, попълнете всички полета", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val distance = distanceText.toDouble()
            val timeMinutes = timeText.toLong()
            val timeMillis = timeMinutes * 60000 // Конвертирай минути в милисекунди

            // Създай обновения Run обект
            val updatedRun = currentRun.copy(
                name = name,
                distanceInKm = distance,
                timeInMillis = timeMillis
            )

            // Запази промените
            viewModel.updateRun(updatedRun)

            Toast.makeText(this, "Пробегът е запазен успешно!", Toast.LENGTH_SHORT).show()
            finish()

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Моля, въведете валидни числови стойности", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteRun() {
        // Изтрий записа
        viewModel.deleteRun(currentRun.id)
        Toast.makeText(this, "Пробегът е изтрит успешно!", Toast.LENGTH_SHORT).show()
        finish()
    }
}