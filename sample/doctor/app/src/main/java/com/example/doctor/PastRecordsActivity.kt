package com.example.doctor

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PastRecordsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvPastRecords: TextView
    private lateinit var btnClearRecords: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_past_records)


        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("PatientData", Context.MODE_PRIVATE)

        // Initialize UI elements
        tvPastRecords = findViewById(R.id.tvPastRecords)
        btnClearRecords = findViewById(R.id.btnClearRecords)

        // Load past records
        loadPastRecords()

        // Clear past records on button click
        btnClearRecords.setOnClickListener {
            clearPastRecords()
        }
    }

    private fun loadPastRecords() {
        val patientName = sharedPreferences.getString("PATIENT_NAME", "No Name Saved")
        val allergies = sharedPreferences.getString("ALLERGIES", "No Allergies Saved")

        tvPastRecords.text = "Patient: $patientName\nAllergies: $allergies"
    }

    private fun clearPastRecords() {
        with(sharedPreferences.edit()) {
            remove("PATIENT_NAME")
            remove("ALLERGIES")
            apply()
        }
        tvPastRecords.text = "No records found."
    }
}
