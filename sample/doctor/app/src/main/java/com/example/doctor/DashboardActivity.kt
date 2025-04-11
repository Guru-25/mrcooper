package com.example.doctor

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val button = findViewById<Button>(R.id.button)
        val btnOpenPatientInfo = findViewById<Button>(R.id.btnOpenPatientInfo)
        val btnOpenMap = findViewById<Button>(R.id.btnOpenMap)
        val btnAppointDoctor = findViewById<Button>(R.id.btnAppointDoctor)
        val btnHealthTips = findViewById<Button>(R.id.btnHealthTips) // Added Health Tips Button

        button.setOnClickListener {
            val popupMenu = PopupMenu(this, button)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.book_appointment -> {
                        Toast.makeText(this, "Booking Appointment", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, AppointmentActivity::class.java))
                        true
                    }
                    R.id.view_past_records -> {
                        Toast.makeText(this, "Viewing Past Records", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, PastRecordsActivity::class.java))
                        true
                    }
                    R.id.view_doctor_profiles -> {
                        Toast.makeText(this, "Viewing Doctor Profiles", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, DoctorProfilesActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        btnOpenPatientInfo.setOnClickListener {
            startActivity(Intent(this, PatientInfoActivity::class.java))
        }

        btnOpenMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        btnAppointDoctor.setOnClickListener {
            startActivity(Intent(this, AppointmentActivity::class.java))
        }

        // Open Health Tips Video Activity
        btnHealthTips.setOnClickListener {
            startActivity(Intent(this, HealthTipsActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this, "Opening Settings", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_share -> {
                Toast.makeText(this, "Sharing App", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_exit -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
