package com.example.hechimtracker.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey



data class WorkoutEndUpdate(
    val endTime: String,
    //distance in meters
    val distance: Double,
    //duration in seconds
    val duration: Int
)
