package com.example.hechimtracker.model.repo

import androidx.room.RoomDatabase
import com.example.hechimtracker.model.database.Point
import com.example.hechimtracker.model.database.Workout
import com.example.hechimtracker.model.database.WorkoutDao
import com.example.hechimtracker.model.database.WorkoutEndUpdate
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class WorkoutRepo @Inject constructor(
    private val workoutDao: WorkoutDao
) {
    suspend fun insertPoint(point: Point) {
        workoutDao.insertPoint(point)
    }
    suspend fun insertWorkout(workout: Workout): Long {
        return workoutDao.insertWorkout(workout)
    }
    suspend fun deleteWorkout(workout: Workout) {
        workoutDao.deleteWorkout(workout)
    }
    suspend fun deleteWorkouts() {
        workoutDao.deleteTable()
    }
    suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout)
    }
    suspend fun updateEndWorkout(
        workoutId: Long,
        endTime: String,
        distance: Double,
        duration: Int
    ) {
        workoutDao.updateWorkoutEnd(
            workoutId = workoutId,
            endTime = endTime,
            distance = distance,
            duration = duration
        )
    }
}