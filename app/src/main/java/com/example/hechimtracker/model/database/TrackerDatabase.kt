package com.example.hechimtracker.model.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities =
    [
        Workout::class,
        Point::class,

    ],
    version = 1,
    exportSchema = true,

)
@TypeConverters(TrackerTypeConverters::class)
abstract class TrackerDatabase: RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}