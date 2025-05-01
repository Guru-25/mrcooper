package com.example.myapplication

import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class MainActivity5 : AppCompatActivity() {
    private lateinit var mapView: MapView

    private lateinit var locationProvideClient: FusedLocationProviderClient
    private lateinit var showLocation: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_main5)
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(15.0)

        val centerPoint = GeoPoint(13.0827, 80.2707)
        mapController.setCenter(centerPoint)

        addFoodRescueLocations()

        locationProvideClient = LocationServices.getFusedLocationProviderClient(this)

        showLocation = findViewById(R.id.show_my_location)
        showLocation.setOnClickListener {
            getYourCurrentLocation()
        }
    }

    private fun addFoodRescueLocations() {
        val locations = listOf(
            Pair("Adaiyar Aanandha Bavan", GeoPoint(13.0830, 80.2715)),
            Pair("A2B", GeoPoint(13.0810, 80.2695)),
            Pair("Vasantha Bavan", GeoPoint(13.0805, 80.2720)),
            Pair("Chennai Food Street", GeoPoint(13.0790, 80.2740)),
            Pair("Fresh Market", GeoPoint(13.0850, 80.2690)),
            Pair("Community Food Bank", GeoPoint(13.0820, 80.2670))
        )

        locations.forEach { (name, point) ->
            val marker = Marker(mapView)
            marker.position = point
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = name
            mapView.overlays.add(marker)
        }

        mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    // location
    private fun getYourCurrentLocation() {
        // Check if we have permission
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                Companion.LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Check if location settings are enabled
        checkLocationSettings()
    }

    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location settings are satisfied, we can get location
            fetchLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, Companion.REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // First try getting last location
        locationProvideClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    Toast.makeText(
                        this,
                        "Latitude: $latitude\nLongitude: $longitude",
                        Toast.LENGTH_LONG
                    ).show()

                    // Get address from coordinates
                    val addressTextView: TextView = findViewById(R.id.addressTextView)
                    addressTextView.text = "Looking up address..."

                    // Use a background thread for geocoding
                    Thread {
                        val address = getAddressFromLatLng(latitude, longitude)
                        runOnUiThread {
                            addressTextView.text = address
                        }
                    }.start()
                } else {
                    // If last location is null, request fresh location
                    Toast.makeText(
                        this,
                        "Getting your current location...",
                        Toast.LENGTH_SHORT
                    ).show()
                    requestNewLocationData()
                }
            }
    }

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
            numUpdates = 1 // Get just one location update
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    Toast.makeText(
                        this@MainActivity5,
                        "Latitude: $latitude\nLongitude: $longitude",
                        Toast.LENGTH_LONG
                    ).show()

                    // Get address from coordinates
                    val addressTextView: TextView = findViewById(R.id.addressTextView)
                    addressTextView.text = "Looking up address..."

                    // Use a background thread for geocoding
                    Thread {
                        val address = getAddressFromLatLng(latitude, longitude)
                        runOnUiThread {
                            addressTextView.text = address
                        }
                    }.start()
                } else {
                    Toast.makeText(
                        this@MainActivity5,
                        "Cannot get your location. Please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()

                    val addressTextView: TextView = findViewById(R.id.addressTextView)
                    addressTextView.text = "Address unavailable"
                }

                // Remove location updates to conserve battery
                locationProvideClient.removeLocationUpdates(this)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationProvideClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Companion.REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // User enabled location settings
                fetchLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location settings are not enabled",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getAddressFromLatLng(lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())

            // For SDK 33+ (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val addressLine = address.getAddressLine(0) ?: ""
                        val locality = address.locality ?: ""
                        val countryName = address.countryName ?: ""

                        runOnUiThread {
                            val addressTextView: TextView = findViewById(R.id.addressTextView)
                            addressTextView.text = "$addressLine, $locality, $countryName"
                        }
                    }
                }
                "Fetching address..."
            } else {
                // For SDK < 33
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val addressLine = address.getAddressLine(0) ?: ""
                    val locality = address.locality ?: ""
                    val countryName = address.countryName ?: ""
                    "$addressLine, $locality, $countryName"
                } else {
                    "No address found"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error fetching address: ${e.message}"
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            // location
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with location
                    checkLocationSettings()
                } else {
                    Toast.makeText(
                        this,
                        "Location permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 123
        private const val LOCATION_PERMISSION_REQUEST_CODE = 909
    }
}