package com.example.trackerapp.domain

import android.util.Log
import com.example.trackerapp.data.repositories.UserLocationRepository
import com.example.trackerapp.entity.Result
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import javax.inject.Inject

class InsertUserLocationInteractor @Inject constructor(
    private val repository: UserLocationRepository,
    private val getIntermediateDistance: GetIntermediateDistanceIteractor,
    private val getSpeed: GetSpeedInteractor
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<Unit> {
        val distance = getIntermediateDistance(LatLng(latitude, longitude))
        val time = LocalDateTime.now()
        val speed = getSpeed(time)
       return repository.insertUserLocation(latitude, longitude, distance, time, speed)
    }
}