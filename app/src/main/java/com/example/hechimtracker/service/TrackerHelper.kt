package com.example.hechimtracker.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import com.example.hechimtracker.model.database.Point
import com.example.hechimtracker.model.local.TrackerNotificationSettings
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

class TrackerHelper(){

    var workoutId: Long = 0
        get() = field
        set(value) {
            field = value
        }

    val STOP_WORKOUT = "STOP_WORKOUT"
    val PAUSE_WORKOUT = "PAUSE_WORKOUT"
    val RESUME_WORKOUT = "RESUME_WORKOUT"

    val settings = TrackerNotificationSettings(
        paused = false,
        time = 0,
        distance = 0.0,
        totalSteps = -1,
        workoutSteps = 0
    )

    private val locationListener = android.location.LocationListener { it -> //handle location change
        if(settings.initialPosition.provider!!.isEmpty()) {
            settings.initialPosition.latitude = it.latitude
            settings.initialPosition.longitude = it.longitude
            settings.initialPosition.speed = it.speed
            settings.initialPosition.altitude = it.altitude
            settings.initialPosition.provider = "updated"
        }
        else {
            if(it.accuracy <= 11) {
                if(!settings.paused) {
                    settings.distance += settings.initialPosition.distanceTo(it)
                    println("INSERTING POINT")
//                    insertPoint(
//                        Point(
//                            workoutId = 1,
//                            latitude = it.latitude,
//                            longitude = it.longitude,
//                            speed = it.speed,
//                        )
//                    )
                }

                settings.initialPosition = it
            }
        }
        //updateNotification()
    }

    fun formatDurationTime(durationSeconds: Int) =
        durationSeconds.seconds.toComponents { hours, minutes, seconds, _ ->
            String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds,
            )
        }

    fun intentActionEquals(action: String, intent: Intent?):Boolean {
        return intent != null && intent.action != null && intent.action == action
    }

}