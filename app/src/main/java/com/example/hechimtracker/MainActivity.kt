package com.example.hechimtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.hechimtracker.service.TrackerService
import com.example.hechimtracker.view_model.DatabaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private val databaseViewModel: DatabaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition(
                condition = {
                    databaseViewModel.isLoading.value
                }
            )
        }
        setContentView(R.layout.activity_main)





        setListener()
        createNotification()

    }

    private fun createNotification() {
        val name = "RideArrival"
        val descriptionText = "Channel for Ride Arriving"
        val importance = NotificationManager.IMPORTANCE_MIN
        val channel = NotificationChannel("RideArrival", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    private fun startService() {
        val serviceIntent = Intent(applicationContext, TrackerService::class.java)
        serviceIntent.action = "START"

        applicationContext.startService(serviceIntent)
    }

    fun setListener() {
//       val view = findViewById<AppCompatButton>(R.id.button)
//        view.setOnClickListener {
//
//            lifecycleScope.launch {
//                //val result = databaseViewModel.deleteAll()
//                startService()
//                //databaseViewModel.getWorkouts()
//            }
//
//        }
    }
}