package com.example.hechimtracker.di

import android.content.Context
import androidx.room.Room
import com.example.hechimtracker.model.database.TrackerDatabase
import com.example.hechimtracker.model.database.WorkoutDao
import com.example.hechimtracker.model.repo.WorkoutRepo
import com.example.hechimtracker.service.TrackerHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkoutModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        TrackerDatabase::class.java, "tracker-database"
    ).build()

    @Singleton
    @Provides
    fun provideWorkoutDao(trackerDatabase: TrackerDatabase): WorkoutDao = trackerDatabase.workoutDao()

    @Singleton
    @Provides
    fun provideWorkoutRepo(workoutDao: WorkoutDao) = WorkoutRepo(workoutDao)

    @Singleton
    @Provides
    fun provideTrackerHelper() = TrackerHelper()
}