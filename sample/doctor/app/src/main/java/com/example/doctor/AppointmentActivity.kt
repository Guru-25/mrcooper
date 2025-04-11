package com.example.doctor

import android.app.*
import android.content.*
import android.telephony.SmsManager
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import java.util.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle

class AppointmentActivity : AppCompatActivity() {

    private lateinit var doctorSpinner: Spinner
    private lateinit var btnSelectDate: Button
    private lateinit var btnSelectTime: Button
    private lateinit var btnConfirmBooking: Button
    private lateinit var txtSelectedDate: TextView
    private lateinit var txtSelectedTime: TextView

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    private lateinit var systemBroadcastReceiver: SystemBroadcastReceiver

    companion object {
        const val CHANNEL_ID = "appointment_channel"
        const val NOTIFICATION_ID = 1
        const val SMS_PERMISSION_CODE = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        // Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // Request SMS permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), SMS_PERMISSION_CODE)
        }

        doctorSpinner = findViewById(R.id.spinnerDoctors)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        btnSelectTime = findViewById(R.id.btnSelectTime)
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking)
        txtSelectedDate = findViewById(R.id.txtSelectedDate)
        txtSelectedTime = findViewById(R.id.txtSelectedTime)

        createNotificationChannel()

        val doctors = arrayOf(
            "Dr. John Smith - Orthopedic",
            "Dr. Sarah Johnson - Cardiologist",
            "Dr. Emily Davis - Dermatologist",
            "Dr. Michael Brown - Pediatrician",
            "Dr. Laura Wilson - Gynecologist"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, doctors)
        doctorSpinner.adapter = adapter

        btnSelectDate.setOnClickListener { showDatePicker() }
        btnSelectTime.setOnClickListener { showTimePicker() }
        btnConfirmBooking.setOnClickListener { showConfirmationDialog() }

        systemBroadcastReceiver = SystemBroadcastReceiver()
    }

    override fun onStart() {
        super.onStart()
        // Register the receiver for battery and airplane mode changes
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED).apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        registerReceiver(systemBroadcastReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        // Unregister the receiver to avoid memory leaks
        unregisterReceiver(systemBroadcastReceiver)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            txtSelectedDate.text = "Selected Date: $selectedDate"
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            txtSelectedTime.text = "Selected Time: $selectedTime"
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun showConfirmationDialog() {
        val selectedDoctor = doctorSpinner.selectedItem.toString()

        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Booking")
        builder.setMessage("Doctor: $selectedDoctor\nDate: $selectedDate\nTime: $selectedTime\n\nDo you want to confirm the appointment?")

        builder.setPositiveButton("Yes") { _, _ ->
            showNotification(selectedDoctor, selectedDate, selectedTime)
            sendSMS("+916381453221", selectedDoctor, selectedDate, selectedTime) // Replace with actual number
            sendWhatsAppMessage("+919025857180", selectedDoctor, selectedDate, selectedTime) // Replace with actual number
            Toast.makeText(this, "Appointment Confirmed!", Toast.LENGTH_SHORT).show()
            finish()
        }

        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Appointment Notifications", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Notification for confirmed appointments"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(doctor: String, date: String, time: String) {
        val intent = Intent(this, AppointmentActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Appointment Confirmed")
            .setContentText("Your appointment with $doctor on $date at $time is confirmed.")
            .setSmallIcon(R.drawable.ic_appointment)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun sendSMS(phoneNumber: String, doctor: String, date: String, time: String) {
        try {
            val message = "Your appointment with $doctor is confirmed on $date at $time."
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                SmsManager.getSmsManagerForSubscriptionId(SmsManager.getDefaultSmsSubscriptionId())
            } else {
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show()
            Log.e("SMS", "Permission error: ${e.message}", e)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to send SMS: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("SMS", "Error sending SMS", e)
        }
    }

    private fun sendWhatsAppMessage(phoneNumber: String, doctor: String, date: String, time: String) {
        val message = "Hello, your appointment with $doctor is confirmed on $date at $time."

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }
}
