package com.example.trackerapp.domain.tracker.final_tracker_data

import com.example.trackerapp.domain.tracker.tracker_data.GetStartTimeInteractor
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetFinalTimeInteractor @Inject constructor(
    private val getStartTime: GetStartTimeInteractor,
) {
    suspend operator fun invoke(): String {
        val timeInMilliseconds =
            ChronoUnit.MILLIS.between(getStartTime(), LocalDateTime.now())
        return DateTimeFormatter
            .ofPattern("HH:mm:ss")
            .withZone(ZoneId.of("UTC"))
            .format(Instant.ofEpochMilli(timeInMilliseconds))
    }
}