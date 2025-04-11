package com.example.doctor

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        btnSignIn.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username == "admin" && password == "1234") {
                // Show Progress Bar
                progressBar.visibility = View.VISIBLE
                btnSignIn.isEnabled = false // Disable button while loading

                // Simulate loading delay (2 seconds)
                Handler().postDelayed({
                    // Hide Progress Bar
                    progressBar.visibility = View.GONE
                    btnSignIn.isEnabled = true

                    // Navigate to DashboardActivity
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("USERNAME", username) // Pass username to next screen
                    startActivity(intent)
                    finish()
                }, 2000)

            } else {
                Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
