package com.example.datetimeapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Notification Permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        createNotificationChannel()

        setContent {
            MaterialTheme {
                ScheduleScreen()
            }
        }
    }

    @Composable
    fun ScheduleScreen() {

        var selectedDate by remember {
            mutableStateOf("Select Date")
        }

        var selectedTime by remember {
            mutableStateOf("Select Time")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Schedule Selector",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Choose Date",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            AndroidView(
                factory = { context ->
                    DatePicker(context).apply {

                        init(
                            year,
                            month,
                            dayOfMonth
                        ) { _, y, m, d ->

                            selectedDate = "$d/${m + 1}/$y"
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Choose Time",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            AndroidView(
                factory = { context ->
                    TimePicker(context).apply {

                        setIs24HourView(true)

                        setOnTimeChangedListener { _, hourOfDay, minute ->

                            selectedTime =
                                String.format(
                                    "%02d:%02d",
                                    hourOfDay,
                                    minute
                                )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Selected Date: $selectedDate"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Selected Time: $selectedTime"
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {

                    val details =
                        "Date: $selectedDate\nTime: $selectedTime"

                    val builder =
                        NotificationCompat.Builder(
                            this@MainActivity,
                            "schedule_channel"
                        )
                            .setSmallIcon(
                                android.R.drawable.ic_dialog_info
                            )
                            .setContentTitle("Schedule Details")
                            .setStyle(
                                NotificationCompat.BigTextStyle()
                                    .bigText(details)
                            )
                            .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                            )

                    val manager =
                        getSystemService(
                            Context.NOTIFICATION_SERVICE
                        ) as NotificationManager

                    manager.notify(1, builder.build())
                }
            ) {
                Text("Show Notification")
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "schedule_channel",
                "Schedule Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }
    }
}