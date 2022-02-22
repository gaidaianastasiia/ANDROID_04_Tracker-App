package com.example.trackerapp.domain.walk

import com.example.trackerapp.domain.tracker.GetStartTimeInteractor
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetWalkDateInteractor @Inject constructor(
    private val getStartTime: GetStartTimeInteractor,
) {
    suspend operator fun invoke(): String {
        return DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm:ss")
            .format(getStartTime())
    }
}