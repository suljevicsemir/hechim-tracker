package com.example.hechimtracker.model.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WorkoutDao {



    @Transaction
    @Query("SELECT * FROM Workout")
    suspend fun getAll(): List<WorkoutPoint>

    @Transaction
    @Query("SELECT * FROM Workout ORDER BY workoutId DESC")
    fun getX(): LiveData<List<WorkoutPoint>>




    @Transaction
    @Query("SELECT * FROM Workout WHERE workoutId = :workoutId")
    suspend fun getWorkout(workoutId: Int): WorkoutPoint



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllPoints(vararg points: Point)

    @Insert
    suspend fun insertPoint(point: Point): Long
    @Insert
    suspend fun insertAllWorkouts(vararg workouts: Workout)

    @Insert
    suspend fun insertWorkout(workout: Workout): Long

    @Delete
    suspend fun deletePoint(point: Point)

    @Delete
    suspend fun deleteWorkout(workout: Workout)
    @Query("DELETE FROM Workout")
    suspend fun deleteTable()

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Query("UPDATE workout SET endTime = :endTime, duration = :duration, distance = :distance WHERE workoutId = :workoutId")
    suspend fun updateWorkoutEnd(workoutId: Long, endTime: String, duration: Int, distance: Double)




}