package com.example.hechimtracker.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hechimtracker.model.database.Point
import com.example.hechimtracker.model.database.TrackerDatabase
import com.example.hechimtracker.model.database.Workout
import com.example.hechimtracker.model.database.WorkoutEndUpdate
import com.example.hechimtracker.model.database.WorkoutPoint
import com.example.hechimtracker.model.local.Resource
import com.example.hechimtracker.model.repo.WorkoutRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DatabaseViewModel @Inject constructor(
    private val trackerDatabase: TrackerDatabase,
    private val workoutRepo: WorkoutRepo
): ViewModel(){

    init {
        viewModelScope.launch {
            delay(3000)
            _isLoading.value = false
        }
    }

    suspend fun updateTest() {
        workoutRepo.updateEndWorkout(
            workoutId = 4,
            endTime = LocalDate.now().toString(),
            duration = 50,
            distance = 150.0
        )
    }

    suspend fun insertWorkout() {
        viewModelScope.launch {
            val result = trackerDatabase.workoutDao().insertAllWorkouts(
                Workout(
                    workoutId = 0,
                    startTime = LocalDate.now().toString()
                )
            )
        }
    }

    suspend fun deleteAll() {
        viewModelScope.launch {
            workoutRepo.deleteWorkouts()
        }

    }

    suspend fun insertPoint() {
        viewModelScope.launch {
            val result = trackerDatabase.workoutDao().insertPoint(
                Point(
                    workoutId = 1
                )
            )
        }
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()







    private val _list = MutableStateFlow<Resource<List<WorkoutPoint>>>(Resource.Loading(null))
    val list = _list.asStateFlow()

    fun getX(): LiveData<List<WorkoutPoint>> {
        return trackerDatabase.workoutDao().getX()
    }

    suspend fun getWorkouts() {

        _list.value = Resource.Loading(null)
        val result = viewModelScope.launch {
            val result = trackerDatabase.workoutDao().getAll()
            trackerDatabase.workoutDao().getX().value
            _list.value = Resource.Success(
                data = result
            )
            _list.value.data!!.forEach {
                println("Workout id: " + it.workout.workoutId.toString())
                println("Distance: ${it.workout.duration}")
            }
        }
    }

    suspend fun getWorkout() {
        viewModelScope.launch {
            val result = trackerDatabase.workoutDao().getWorkout(4)
            println(result.points.size)
            result.points.forEach {
                println(it.toString())
            }
        }
    }
}