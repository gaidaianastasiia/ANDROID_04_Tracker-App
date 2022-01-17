package com.example.trackerapp.domain

import android.util.Log
import com.google.android.gms.common.util.NumberUtils
import java.lang.Math.round
import java.lang.StrictMath.round
import java.math.RoundingMode
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
        val timeIntervalInHours = (timeIntervalInSeconds / 60) / 60
        val distanceInMeters = getLastUserLocation()?.distance ?: 0.0
        val distanceInKilometers = distanceInMeters / 1000

        val result = if (distanceInKilometers == 0.0 || timeIntervalInHours == 0.0) {
            0.00
        } else {
            distanceInKilometers.div(timeIntervalInHours)
        }
        return  (result * 10.0).roundToInt() / 10.0
    }
}