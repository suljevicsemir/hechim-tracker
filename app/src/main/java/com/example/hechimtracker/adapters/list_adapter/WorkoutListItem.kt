package com.example.hechimtracker.adapters.list_adapter

import com.example.hechimtracker.model.database.Workout
import com.example.hechimtracker.model.database.WorkoutPoint

data class WorkoutListItem(
    val workout: WorkoutPoint,
    val onClick: (() -> Unit)
)
