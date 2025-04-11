package com.example.doctor

import android.content.Context
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class DoctorProfilesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_profiles)

        val listView: ListView = findViewById(R.id.listViewDoctors)

        // List of doctors
        val doctors = listOf(
            Doctor("Dr. John Smith", "Orthopedic Specialist", R.drawable.doctor1),
            Doctor("Dr. Emily Davis", "Cardiologist", R.drawable.doctor2),
            Doctor("Dr. Robert Wilson", "Dermatologist", R.drawable.doctor3),
            Doctor("Dr. Olivia Brown", "Pediatrician", R.drawable.doctor4),
            Doctor("Dr. Michael Johnson", "Gynecologist", R.drawable.doctor5)
        )

        // Adapter
        val adapter = DoctorAdapter(this, doctors)
        listView.adapter = adapter
    }
}
