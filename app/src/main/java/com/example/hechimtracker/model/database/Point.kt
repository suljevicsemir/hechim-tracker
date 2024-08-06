package com.example.hechimtracker.model.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity()
data class Point(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val speed: Float? = null,
    val workoutId: Long
): Parcelable {

    fun toLatLng(): LatLng {
        return LatLng(latitude!!, longitude!!)
    }
}
