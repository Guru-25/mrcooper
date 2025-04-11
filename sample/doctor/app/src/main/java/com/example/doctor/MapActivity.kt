package com.example.doctor

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var locationTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Load OSM configurations
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        // Initialize UI components
        mapView = findViewById(R.id.mapView)
        locationTextView = findViewById(R.id.locationTextView)
        locationTextView.text = "Specialist Hospitals Nearby"

        // Setup map properties
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Default center location
        val centerLocation = GeoPoint(40.7128, -74.0060) // New York
        mapView.controller.setZoom(12.0)
        mapView.controller.setCenter(centerLocation)

        // List of hospital locations with specialties
        val hospitals = listOf(
            Pair(GeoPoint(40.7138, -74.0070), "Ortho Specialist Hospital"),
            Pair(GeoPoint(40.7145, -74.0035), "Cardiac Specialist Hospital"),
            Pair(GeoPoint(40.7102, -74.0025), "Skin Specialist Hospital"),
            Pair(GeoPoint(40.7150, -74.0050), "Children's Specialist Hospital"),
            Pair(GeoPoint(40.7168, -74.0090), "Gynaecologist Hospital")
        )

        // Add markers for hospitals
        for (hospital in hospitals) {
            val marker = Marker(mapView)
            marker.position = hospital.first
            marker.title = hospital.second
            mapView.overlays.add(marker)
        }
    }
}
