package com.example.event

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

data class Event(
    val title: String,
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?
) {
    fun titleWithDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val locationText =
            if (latitude != null && longitude != null) "\nLocation: ($latitude, $longitude)" else ""
        return "$title\n${sdf.format(Date(timestamp))}$locationText"
    }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("title", title)
            put("timestamp", timestamp)
            if (latitude != null && longitude != null) {
                put("latitude", latitude)
                put("longitude", longitude)
            }
        }
    }

    companion object {
        fun fromJson(obj: JSONObject): Event {
            val lat = if (obj.has("latitude")) obj.getDouble("latitude") else null
            val lon = if (obj.has("longitude")) obj.getDouble("longitude") else null
            return Event(obj.getString("title"), obj.getLong("timestamp"), lat, lon)
        }
    }
}

class DashboardActivity : AppCompatActivity() {

    private lateinit var eventListView: ListView
    private lateinit var addEventBtn: Button
    private val events = mutableListOf<Event>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sharedPreferences: SharedPreferences
    private val channelId = "event_alert_channel"
    private val notifPermissionRequestCode = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm_prefs", MODE_PRIVATE))
        setContentView(R.layout.activity_dashboard)

        eventListView = findViewById(R.id.eventListView)
        addEventBtn = findViewById(R.id.addEventBtn)
        sharedPreferences = getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)

        createNotificationChannel()
        requestPermissionsIfNeeded()
        loadEvents()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, events.map { it.titleWithDate() }.toMutableList())
        eventListView.adapter = adapter

        addEventBtn.setOnClickListener { showEventDialog() }

        eventListView.setOnItemClickListener { _, _, position, _ ->
            showOptionsDialog(position)
        }
    }

    private fun showEventDialog(editIndex: Int? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null)
        val titleEdit = dialogView.findViewById<EditText>(R.id.etTitle)
        val dateTimeBtn = dialogView.findViewById<Button>(R.id.btnPickDate)
        val latEdit = dialogView.findViewById<EditText>(R.id.etLat)
        val lonEdit = dialogView.findViewById<EditText>(R.id.etLon)
        val viewMapBtn = dialogView.findViewById<Button>(R.id.btnViewMap)
        val mapView = dialogView.findViewById<MapView>(R.id.mapView)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        val mapController = mapView.controller
        mapController.setZoom(5.0)
        mapController.setCenter(GeoPoint(20.5937, 78.9629)) // Default to center of India

        val calendar = Calendar.getInstance()
        var timestamp = calendar.timeInMillis

        viewMapBtn.setOnClickListener {
            val latStr = latEdit.text.toString()
            val lonStr = lonEdit.text.toString()
            if (latStr.isNotBlank() && lonStr.isNotBlank()) {
                val lat = latStr.toDoubleOrNull()
                val lon = lonStr.toDoubleOrNull()
                if (lat != null && lon != null) {
                    val point = GeoPoint(lat, lon)
                    mapController.setZoom(15.0)
                    mapController.setCenter(point)

                    mapView.overlays.clear()
                    val marker = Marker(mapView)
                    marker.position = point
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = "Event Location"
                    mapView.overlays.add(marker)
                    mapView.invalidate()
                }
            } else {
                Toast.makeText(this, "Please enter valid latitude and longitude", Toast.LENGTH_SHORT).show()
            }
        }

        dateTimeBtn.setOnClickListener {
            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                val timePicker = TimePickerDialog(this, { _, hour, minute ->
                    calendar.set(year, month, day, hour, minute)
                    timestamp = calendar.timeInMillis
                    Toast.makeText(this, "Time set!", Toast.LENGTH_SHORT).show()
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
                timePicker.show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        editIndex?.let {
            val event = events[it]
            titleEdit.setText(event.title)
            timestamp = event.timestamp
            event.latitude?.let { lat -> latEdit.setText(lat.toString()) }
            event.longitude?.let { lon -> lonEdit.setText(lon.toString()) }

            if (event.latitude != null && event.longitude != null) {
                val point = GeoPoint(event.latitude, event.longitude)
                mapController.setZoom(15.0)
                mapController.setCenter(point)

                val marker = Marker(mapView)
                marker.position = point
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = "Event Location"
                mapView.overlays.add(marker)
            }
        }

        AlertDialog.Builder(this)
            .setTitle(if (editIndex == null) "Add Event" else "Edit Event")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = titleEdit.text.toString()
                val lat = latEdit.text.toString().toDoubleOrNull()
                val lon = lonEdit.text.toString().toDoubleOrNull()
                if (title.isNotBlank()) {
                    val newEvent = Event(title, timestamp, lat, lon)
                    if (editIndex == null) {
                        events.add(newEvent)
                    } else {
                        events[editIndex] = newEvent
                    }
                    scheduleNotification(title, timestamp)
                    saveEvents()
                    updateList()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showOptionsDialog(position: Int) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(this)
            .setTitle("Choose an option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEventDialog(position)
                    1 -> {
                        events.removeAt(position)
                        saveEvents()
                        updateList()
                    }
                }
            }
            .show()
    }

    private fun updateList() {
        adapter.clear()
        adapter.addAll(events.map { it.titleWithDate() })
        adapter.notifyDataSetChanged()
    }

    private fun saveEvents() {
        val jsonArray = JSONArray()
        events.forEach { jsonArray.put(it.toJson()) }
        sharedPreferences.edit().putString("events", jsonArray.toString()).apply()
    }

    private fun loadEvents() {
        val jsonString = sharedPreferences.getString("events", null) ?: return
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            events.add(Event.fromJson(jsonArray.getJSONObject(i)))
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Event Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for event reminders"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification(title: String, timestamp: Long) {
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("title", title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            timestamp.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            Toast.makeText(this, "Exact alarm permission required!", Toast.LENGTH_LONG).show()
            return
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
    }

    private fun requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    notifPermissionRequestCode
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!(getSystemService(AlarmManager::class.java).canScheduleExactAlarms())) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }
}
