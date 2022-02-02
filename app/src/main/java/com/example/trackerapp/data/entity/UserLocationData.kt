package com.example.trackerapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class UserLocationData(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo val latitude: Double,
    @ColumnInfo val longitude: Double,
    @ColumnInfo val distance: Double,
    @ColumnInfo val time: LocalDateTime,
    @ColumnInfo val speed: Double,
) {
    constructor(
        latitude: Double,
        longitude: Double,
        distance: Double,
        time: LocalDateTime,
        speed: Double,
    ) : this(0, latitude, longitude, distance, time, speed)
}
