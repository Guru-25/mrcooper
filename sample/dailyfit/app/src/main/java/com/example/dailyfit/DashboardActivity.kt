package com.example.dailyfit

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class DashboardActivity : AppCompatActivity() {

    private lateinit var etSteps: EditText
    private lateinit var etCalories: EditText
    private lateinit var etWater: EditText
    private lateinit var btnSave: Button
    private lateinit var summaryText: TextView
    private lateinit var welcomeText: TextView

    private val CHANNEL_ID = "water_reminder_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        etSteps = findViewById(R.id.etSteps)
        etCalories = findViewById(R.id.etCalories)
        etWater = findViewById(R.id.etWater)
        btnSave = findViewById(R.id.btnSave)
        summaryText = findViewById(R.id.summaryText)
        welcomeText = findViewById(R.id.welcomeText)

        val name = intent.getStringExtra("username")
        welcomeText.text = "Welcome, $name!"

        // Load saved data
        val prefs = getSharedPreferences("HealthData", Context.MODE_PRIVATE)
        etSteps.setText(prefs.getString("steps", ""))
        etCalories.setText(prefs.getString("calories", ""))
        etWater.setText(prefs.getString("water", ""))

        // Request Notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            } else {
                scheduleWaterReminder()
            }
        } else {
            scheduleWaterReminder()
        }

        btnSave.setOnClickListener {
            val steps = etSteps.text.toString()
            val calories = etCalories.text.toString()
            val water = etWater.text.toString()

            if (steps.isNotEmpty() && calories.isNotEmpty() && water.isNotEmpty()) {
                val editor = getSharedPreferences("HealthData", Context.MODE_PRIVATE).edit()
                editor.putString("steps", steps)
                editor.putString("calories", calories)
                editor.putString("water", water)
                editor.apply()

                summaryText.text = "Today's Summary:\nSteps: $steps\nCalories: $calories\nWater: $water L"

                Toast.makeText(this, "Data Saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scheduleWaterReminder() {
        createNotificationChannel()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Make sure to add this icon
            .setContentTitle("Hydration Reminder")
            .setContentText("Don't forget to drink water!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(this)

        // Show notification immediately for demo purposes
        notificationManager.notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Water Reminder"
            val descriptionText = "Channel for water drinking reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
