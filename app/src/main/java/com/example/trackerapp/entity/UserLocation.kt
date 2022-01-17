package com.example.trackerapp.entity

import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class UserLocation(
    val location: LatLng,
    val distance: Double,
    val time: LocalDateTime,
    val speed: Double
)
