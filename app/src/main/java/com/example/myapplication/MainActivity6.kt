package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity6 : AppCompatActivity() {

    // Firebase Database Reference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val tvData = findViewById<TextView>(R.id.tvData)

        // Initialize Firebase Database Reference
        database = FirebaseDatabase.getInstance().reference.child("Users")

        // Save Data to Firebase when button is pressed
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                val userId = database.push().key // Generate a unique key
                val user = User(name, email) // Create a User object

                if (userId != null) {
                    database.child(userId).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Data Saved Successfully!", Toast.LENGTH_SHORT).show()
                            etName.text.clear()
                            etEmail.text.clear()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to Save Data", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            }
        }

        // Retrieve Data from Firebase and display in the TextView
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stringBuilder = StringBuilder()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    stringBuilder.append("Name: ${user?.name}\nEmail: ${user?.email}\n\n")
                }
                tvData.text = stringBuilder.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity6, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// User Data Model
data class User(val name: String = "", val email: String = "")