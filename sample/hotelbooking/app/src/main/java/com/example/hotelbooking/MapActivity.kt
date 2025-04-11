package com.example.hotelbooking

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private val CHANNEL_ID = "hotel_booking_channel"
    private val NOTIFICATION_ID = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_map)

        createNotificationChannel()
        requestNotificationPermission()

        mapView = findViewById(R.id.mapView)
        mapView.setMultiTouchControls(true)

        val location = intent.getStringExtra("location") ?: "Chennai"
        val (centerPoint, hotels) = getHotelsForLocation(location)

        val controller = mapView.controller
        controller.setZoom(15.0)
        controller.setCenter(centerPoint)

        hotels.forEach { (name, point) ->
            val marker = Marker(mapView)
            marker.position = point
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = name
            marker.setOnMarkerClickListener { _, _ ->
                AlertDialog.Builder(this)
                    .setTitle("Booking Confirmed")
                    .setMessage("Hotel room at $name is booked!")
                    .setPositiveButton("OK") { _, _ ->
                        showBookingNotification(name)
                    }
                    .show()
                true
            }
            mapView.overlays.add(marker)
        }
    }

    private fun showBookingNotification(hotelName: String) {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your own notification icon
            .setContentTitle("Hotel Booked!")
            .setContentText("You booked $hotelName at $currentTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MapActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Hotel Booking Notifications"
            val descriptionText = "Notifications for hotel bookings"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private fun getHotelsForLocation(location: String): Pair<GeoPoint, List<Pair<String, GeoPoint>>> {
        return when (location) {
            "Chennai" -> GeoPoint(13.0827, 80.2707) to listOf(
                "Chennai Grand Inn" to GeoPoint(13.0830, 80.2715),
                "Ocean Residency" to GeoPoint(13.0810, 80.2695),
                "Sun City Stay" to GeoPoint(13.0805, 80.2720)
            )
            "Bangalore" -> GeoPoint(12.9716, 77.5946) to listOf(
                "Bangalore Comforts" to GeoPoint(12.9720, 77.5950),
                "Silicon Hotel" to GeoPoint(12.9700, 77.5930),
                "IT Hub Inn" to GeoPoint(12.9730, 77.5965)
            )
            "Hyderabad" -> GeoPoint(17.3850, 78.4867) to listOf(
                "Pearl Palace" to GeoPoint(17.3860, 78.4875),
                "Charminar Residency" to GeoPoint(17.3840, 78.4855),
                "Hitech Suites" to GeoPoint(17.3835, 78.4880)
            )
            else -> GeoPoint(13.0827, 80.2707) to emptyList()
        }
    }
}
