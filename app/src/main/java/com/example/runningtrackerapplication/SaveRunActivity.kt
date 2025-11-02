package com.example.runningtrackerapplication

import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.textfield.TextInputEditText

class SaveRunActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var durationText: TextView
    private lateinit var distanceText: TextView
    private lateinit var runNameInput: TextInputEditText
    private lateinit var runDescriptionInput: TextInputEditText
    private lateinit var saveButton: Button

    private var duration: Long = 0
    private var distance: Double = 0.0
    private lateinit var routePoints: List<LatLng>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_run)

        duration = intent.getLongExtra("DURATION", 0)
        distance = intent.getDoubleExtra("DISTANCE", 0.0)
        @Suppress("UNCHECKED_CAST")
        routePoints = intent.getSerializableExtra("ROUTE_POINTS") as? List<LatLng> ?: emptyList()

        initViews()
        setupMap()
        populateData()
        setupClickListeners()
    }

    private fun initViews() {
        durationText = findViewById(R.id.durationText)
        distanceText = findViewById(R.id.distanceText)
        runNameInput = findViewById(R.id.runNameInput)
        runDescriptionInput = findViewById(R.id.runDescriptionInput)
        saveButton = findViewById(R.id.saveButton)
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.routeMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        drawRouteOnMap()
    }

    private fun drawRouteOnMap() {
        if (routePoints.size < 2) return

        val polylineOptions = PolylineOptions()
            .addAll(routePoints)
            .color(Color.BLUE)
            .width(8f)
        googleMap.addPolyline(polylineOptions)

        googleMap.addMarker(
            MarkerOptions()
                .position(routePoints.first())
                .title("Начало")
        )

        googleMap.addMarker(
            MarkerOptions()
                .position(routePoints.last())
                .title("Край")
        )

        val bounds = LatLngBounds.builder()
        routePoints.forEach { point ->
            bounds.include(point)
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50))
    }

    private fun populateData() {
        val date = SimpleDateFormat("dd.MM", Locale.getDefault()).format(Date())
        val formattedDistance = String.format(Locale.getDefault(), "%.2f", distance)
        val autoName = "Тренировка $date ${formattedDistance}км"

        runNameInput.setText(autoName)

        durationText.text = formatTimeForDisplay(duration)
        distanceText.text = String.format(Locale.getDefault(), "%.2f км", distance)
    }

    private fun formatTimeForDisplay(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d мин", minutes, seconds)
    }

    private fun setupClickListeners() {
        saveButton.setOnClickListener {
            saveRun()
        }
    }

    private fun saveRun() {
        val name = runNameInput.text.toString().trim()
        val description = runDescriptionInput.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Моля, въведете име на тренировката", Toast.LENGTH_SHORT).show()
            return
        }

        val routePointsForFirebase = routePoints.map { latLng ->
            mapOf(
                "latitude" to latLng.latitude,
                "longitude" to latLng.longitude
            )
        }

        val run = Run(
            name = name,
            description = description,
            distanceInKm = distance,
            timeInMillis = duration,
            timestamp = System.currentTimeMillis(),
            routePoints = routePointsForFirebase
        )

        MainViewModel().addRun(run)

        Toast.makeText(this, "Тренировката е запазена успешно!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}