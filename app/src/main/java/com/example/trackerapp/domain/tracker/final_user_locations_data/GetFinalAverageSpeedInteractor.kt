package com.example.trackerapp.domain.tracker.final_user_locations_data

import com.example.trackerapp.domain.tracker.GetStartTimeInteractor
import com.example.trackerapp.utils.DEFAULT_SPEED
import com.example.trackerapp.utils.METERS_IN_KILOMETERS
import com.example.trackerapp.utils.MINUTES_IN_HOUR
import com.example.trackerapp.utils.SECONDS_IN_MINUTE
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt

class GetFinalAverageSpeedInteractor @Inject constructor(
    private val getFinalDistance: GetFinalDistanceInteractor,
    private val getStartTime: GetStartTimeInteractor,
) {
    suspend operator fun invoke() : Double {
        val timeIntervalInSeconds =
            ChronoUnit.SECONDS.between(getStartTime(), LocalDateTime.now()).toDouble()
        val timeIntervalInHours = (timeIntervalInSeconds / SECONDS_IN_MINUTE) / MINUTES_IN_HOUR
        val finalDistance = getFinalDistance()

        return if (finalDistance != DEFAULT_SPEED) {
            val finalAverageSpeed = (finalDistance / METERS_IN_KILOMETERS) / timeIntervalInHours
            (finalAverageSpeed * 10.0).roundToInt() / 10.0
        } else {
            DEFAULT_SPEED
        }
    }
}