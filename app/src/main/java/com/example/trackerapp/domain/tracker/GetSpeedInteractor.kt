package com.example.trackerapp.domain.tracker

import com.example.trackerapp.utils.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt

class GetSpeedInteractor @Inject constructor(
    private val getLastUserLocation: GetLastUserLocationInteractor,
) {
    suspend operator fun invoke(endPointTime: LocalDateTime): Double {
        val startPointTime = getLastUserLocation()?.time ?: endPointTime
        val timeIntervalInSeconds = ChronoUnit.SECONDS.between(startPointTime, endPointTime).toDouble()
        val timeIntervalInHours = (timeIntervalInSeconds / SECONDS_IN_MINUTE) / MINUTES_IN_HOUR
        val distanceInMeters = getLastUserLocation()?.distance ?: DEFAULT_DISTANCE
        val distanceInKilometers = distanceInMeters / METERS_IN_KILOMETERS

        val result = if (distanceInKilometers == DEFAULT_DISTANCE || timeIntervalInHours == DEFAULT_TIME) {
            DEFAULT_SPEED
        } else {
            distanceInKilometers.div(timeIntervalInHours)
        }
        return  (result * 10.0).roundToInt() / 10.0
    }
}