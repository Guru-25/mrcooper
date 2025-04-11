package com.example.doctor

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PatientInfoActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etPatientName: EditText
    private lateinit var etAllergies: EditText
    private lateinit var btnSave: Button
    private lateinit var tvSavedData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_patient_info)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("PatientData", Context.MODE_PRIVATE)

        // Initialize UI elements
        etPatientName = findViewById(R.id.etPatientName)
        etAllergies = findViewById(R.id.etAllergies)
        btnSave = findViewById(R.id.btnSave)
        tvSavedData = findViewById(R.id.tvSavedData)

        // Load saved data
        loadPatientData()

        // Save data on button click
        btnSave.setOnClickListener {
            savePatientData()
        }
    }

    private fun savePatientData() {
        val patientName = etPatientName.text.toString()
        val allergies = etAllergies.text.toString()

        with(sharedPreferences.edit()) {
            putString("PATIENT_NAME", patientName)
            putString("ALLERGIES", allergies)
            apply()
        }

        loadPatientData() // Refresh UI
    }

    private fun loadPatientData() {
        val patientName = sharedPreferences.getString("PATIENT_NAME", "No Name Saved")
        val allergies = sharedPreferences.getString("ALLERGIES", "No Allergies Saved")

        tvSavedData.text = "Patient: $patientName\nAllergies: $allergies"
    }
}
