package com.example.runningtrackerapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.Polyline

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var timer: Chronometer
    private lateinit var statusText: TextView

    private var isTracking = false
    private var startTime: Long = 0
    private val pathPoints = mutableListOf<LatLng>()
    private var routePolyline: Polyline? = null

    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                enableLocationFeatures()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                enableLocationFeatures()
            }
            else -> {
                statusText.text = "Локацията е необходима за проследяване на маршрута"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        initViews()
        setupMap()
        setupClickListeners()
    }

    private fun initViews() {
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        timer = findViewById(R.id.timer)
        statusText = findViewById(R.id.statusText)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapsFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        requestLocationPermissions()
    }

    private fun requestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableLocationFeatures()
            }
            else -> {
                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
        }
    }

    private fun enableLocationFeatures() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                googleMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                statusText.text = "Грешка с правата за локация"
            }
        }
        statusText.text = "Готов за старт"
    }

    private fun setupClickListeners() {
        startButton.setOnClickListener {
            startTracking()
        }

        stopButton.setOnClickListener {
            stopTracking()
        }
    }

    private fun startTracking() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            statusText.text = "Нямате права за локация. Моля, дайте правата."
            requestLocationPermissions()
            return
        }

        isTracking = true
        startTime = System.currentTimeMillis()
        pathPoints.clear()

        timer.base = SystemClock.elapsedRealtime()
        timer.start()
        timer.visibility = TextView.VISIBLE

        startButton.isEnabled = false
        stopButton.isEnabled = true
        statusText.text = "Тренировката започна"

        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        pathPoints.add(currentLatLng)

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                        googleMap.addMarker(
                            MarkerOptions()
                                .position(currentLatLng)
                                .title("Начална точка")
                        )

                        startLocationUpdates()
                    }
                }
        }
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).setMinUpdateIntervalMillis(2000L)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!isTracking) return

                locationResult.lastLocation?.let { location ->
                    val newLatLng = LatLng(location.latitude, location.longitude)
                    pathPoints.add(newLatLng)
                    updateRouteOnMap()
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest!!,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun updateRouteOnMap() {
        routePolyline?.remove()

        if (pathPoints.size > 1) {
            routePolyline = googleMap.addPolyline(
                PolylineOptions()
                    .addAll(pathPoints)
                    .color(Color.BLUE)
                    .width(8f)
            )

            val lastPoint = pathPoints.last()
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(lastPoint))
        }
    }

    private fun stopTracking() {
        isTracking = false
        timer.stop()

        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }

        val totalDistance = calculateTotalDistance()

        val intent = Intent(this, SaveRunActivity::class.java).apply {
            putExtra("DURATION", System.currentTimeMillis() - startTime)
            putExtra("DISTANCE", totalDistance)
            putExtra("ROUTE_POINTS", ArrayList(pathPoints))
        }
        startActivity(intent)
        finish()
    }

    private fun calculateTotalDistance(): Double {
        var totalDistance = 0.0
        for (i in 1 until pathPoints.size) {
            totalDistance += calculateDistance(pathPoints[i-1], pathPoints[i])
        }
        return totalDistance
    }

    private fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            point1.latitude, point1.longitude,
            point2.latitude, point2.longitude,
            results
        )
        return results[0].toDouble() / 1000.0
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}