package com.example.hechimtracker.model.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
data class Workout(
    @PrimaryKey(autoGenerate = true) val workoutId: Long,
    val startTime: String,
    val endTime: String? = null,
    //distance in meters
    val distance: Double? = null,
    //duration in seconds
    val duration: Int? = null
): Parcelable
