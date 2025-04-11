package com.example.doctor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationCompat

class SystemBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "SystemBroadcastChannel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {

            Intent.ACTION_BATTERY_CHANGED -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)

                // Always show toast with current battery level
                Toast.makeText(context, "Battery Level: $level%", Toast.LENGTH_SHORT).show()

                // If battery is low, also show notification
                if (level in 1..19) {
                    showBatteryLowNotification(context, level)
                }
            }

            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                val isAirplaneModeOn = Settings.Global.getInt(
                    context.contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON, 0
                ) != 0

                val message = if (isAirplaneModeOn) {
                    "Airplane Mode is ON"
                } else {
                    "Airplane Mode is OFF"
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showBatteryLowNotification(context: Context, level: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "System Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies about low battery and airplane mode changes"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("Battery Low")
            .setContentText("Battery is at $level%. Please charge your device.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
