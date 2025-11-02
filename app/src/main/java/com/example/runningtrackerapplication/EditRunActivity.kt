package com.example.runningtrackerapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.LatLngBounds
import android.graphics.Color
import com.google.android.gms.maps.model.MarkerOptions

class EditRunActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var editName: TextInputEditText
    private lateinit var editDescription: TextInputEditText
    private lateinit var editDistance: TextInputEditText
    private lateinit var editTime: TextInputEditText
    private lateinit var timestampText: TextView
    private lateinit var saveButton: Button
    private lateinit var deleteButton: TextView

    private lateinit var currentRun: Run
    private lateinit var viewModel: MainViewModel

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_run)

        viewModel = MainViewModel()

        currentRun = intent.getSerializableExtra("RUN_EXTRA") as? Run ?: run {
            Toast.makeText(this, "Грешка при зареждане на данните", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        populateData()
        setupClickListeners()
        setupMap()
    }

    private fun initViews() {
        editName = findViewById(R.id.editName)
        editDescription = findViewById(R.id.editDescription)
        editDistance = findViewById(R.id.editDistance)
        editTime = findViewById(R.id.editTime)
        timestampText = findViewById(R.id.timestampText)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)
    }

    private fun populateData() {
        editName.setText(currentRun.name)
        editDescription.setText(currentRun.description)
        editDistance.setText(String.format(Locale.getDefault(), "%.2f", currentRun.distanceInKm))

        val totalSeconds = currentRun.timeInMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        editTime.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds))

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

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.routeMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isZoomGesturesEnabled = true
        googleMap.uiSettings.isScrollGesturesEnabled = true

        displayRoute()
    }

    private fun displayRoute() {
        if (currentRun.routePoints.isEmpty()) {
            val defaultLocation = LatLng(42.6977, 23.3219)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
            return
        }

        val latLngPoints = currentRun.routePoints.map { pointMap ->
            val lat = pointMap["latitude"] ?: 0.0
            val lng = pointMap["longitude"] ?: 0.0
            LatLng(lat, lng)
        }

        val polylineOptions = PolylineOptions()
            .addAll(latLngPoints)
            .width(12f)
            .color(Color.parseColor("#2196F3"))
            .geodesic(true)

        googleMap.addPolyline(polylineOptions)

        val startPoint = latLngPoints.first()
        val endPoint = latLngPoints.last()

        val startMarker = MarkerOptions()
            .position(startPoint)
            .title("Начало")

        val endMarker = MarkerOptions()
            .position(endPoint)
            .title("Край")

        googleMap.addMarker(startMarker)
        googleMap.addMarker(endMarker)

        val bounds = LatLngBounds.builder()
        latLngPoints.forEach { point ->
            bounds.include(point)
        }

        try {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
        } catch (e: Exception) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15f))
        }
    }

    private fun saveRun() {
        val name = editName.text.toString().trim()
        val description = editDescription.text.toString().trim()
        val distanceText = editDistance.text.toString().trim()
        val timeText = editTime.text.toString().trim()

        if (name.isEmpty() || distanceText.isEmpty() || timeText.isEmpty()) {
            Toast.makeText(this, "Моля, попълнете всички полета", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val distance = distanceText.toDouble()

            val timeParts = timeText.split(":")
            val minutes = timeParts[0].toLong()
            val seconds = timeParts[1].toLong()
            val timeMillis = (minutes * 60 + seconds) * 1000

            val updatedRun = currentRun.copy(
                name = name,
                description = description,
                distanceInKm = distance,
                timeInMillis = timeMillis
            )

            viewModel.updateRun(updatedRun)

            Toast.makeText(this, "Пробегът е запазен успешно!", Toast.LENGTH_SHORT).show()
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Моля, въведете валидни стойности", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteRun() {
        viewModel.deleteRun(currentRun.id)
        Toast.makeText(this, "Пробегът е изтрит успешно!", Toast.LENGTH_SHORT).show()
        finish()
    }
}