package com.example.hotelbooking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var showHotelsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        radioGroup = findViewById(R.id.radioGroup)
        showHotelsButton = findViewById(R.id.showHotelsButton)

        showHotelsButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            val location = when (selectedId) {
                R.id.radioChennai -> "Chennai"
                R.id.radioBangalore -> "Bangalore"
                R.id.radioHyderabad -> "Hyderabad"
                else -> ""
            }

            if (location.isNotEmpty()) {
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra("location", location)
                startActivity(intent)
            }
        }
    }
}
