package com.example.trackerapp.domain

import com.example.trackerapp.utils.DEFAULT_SPEED
import com.example.trackerapp.utils.METERS_IN_KILOMETERS
import com.example.trackerapp.utils.MINUTES_IN_HOUR
import com.example.trackerapp.utils.SECONDS_IN_MINUTE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt

class GetAverageSpeedInteractor @Inject constructor(
    private val getCoveredDistance: GetCoveredDistanceInteractor,
    private val getStartTime: GetStartTimeInteractor,
) {
    suspend operator fun invoke(): Flow<Double> {
        val timeIntervalInSeconds =
            ChronoUnit.SECONDS.between(getStartTime(), LocalDateTime.now()).toDouble()
        val timeIntervalInHours = (timeIntervalInSeconds / SECONDS_IN_MINUTE) / MINUTES_IN_HOUR
        return getCoveredDistance()
            .map { if (it != DEFAULT_SPEED) (it / METERS_IN_KILOMETERS) / timeIntervalInHours else DEFAULT_SPEED }
            .map { (it * 10.0).roundToInt() / 10.0 }
    }
} 