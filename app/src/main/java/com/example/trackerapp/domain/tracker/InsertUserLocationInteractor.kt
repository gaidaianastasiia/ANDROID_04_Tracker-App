package com.example.trackerapp.domain.tracker

import com.example.trackerapp.data.repositories.user_location.UserLocationRepository
import com.example.trackerapp.entity.Result
import com.example.trackerapp.entity.UserLocation
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import javax.inject.Inject

class InsertUserLocationInteractor @Inject constructor(
    private val repository: UserLocationRepository,
    private val getIntermediateDistance: GetIntermediateDistanceIteractor,
    private val getSpeed: GetSpeedInteractor
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<Unit> {
        val location = LatLng(latitude, longitude)
        val distance = getIntermediateDistance(location)
        val time = LocalDateTime.now()
        val speed = getSpeed(time)
        val userLocation = UserLocation(location, distance, time, speed)
       return repository.insertUserLocation(userLocation)
    }
}