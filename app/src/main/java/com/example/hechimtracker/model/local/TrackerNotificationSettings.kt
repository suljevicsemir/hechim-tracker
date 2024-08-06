package com.example.hechimtracker.model.local



import android.location.Location

data class TrackerNotificationSettings(
    var time: Int,
    var distance: Double,
    var paused: Boolean,
    var totalSteps: Int = -1,
    var workoutSteps: Int,
    var initialPosition: Location = Location("")
)

