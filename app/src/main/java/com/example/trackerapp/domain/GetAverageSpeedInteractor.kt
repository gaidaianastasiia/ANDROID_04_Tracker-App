package com.example.trackerapp.domain

import android.util.Log
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
        val timeIntervalInHours = (timeIntervalInSeconds / 60) / 60
        return getCoveredDistance()
            .map { if (it != 0.0) (it / 1000) / timeIntervalInHours else 0.0 }
            .map { Log.d("###", "$it") }
            .map { (it * 10.0).roundToInt() / 10.0 }
    }
} 