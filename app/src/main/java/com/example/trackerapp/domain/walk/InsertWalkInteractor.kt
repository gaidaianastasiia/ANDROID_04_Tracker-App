package com.example.trackerapp.domain.walk

import android.content.Context
import android.graphics.Bitmap
import com.example.trackerapp.data.repositories.walk.WalkRepository
import com.example.trackerapp.domain.tracker.final_user_locations_data.GetFinalAverageSpeedInteractor
import com.example.trackerapp.domain.tracker.final_user_locations_data.GetFinalDistanceInteractor
import com.example.trackerapp.domain.tracker.final_user_locations_data.GetFinalTimeInteractor
import com.example.trackerapp.entity.NewWalk
import com.example.trackerapp.entity.Result
import com.example.trackerapp.utils.ImageManager
import java.util.*

import javax.inject.Inject

private const val BASE_IMAGE_NAME = "map"

class InsertWalkInteractor @Inject constructor(
    private val imageManager: ImageManager,
    private val getWalkDate: GetWalkDateInteractor,
    private val getFinalTime: GetFinalTimeInteractor,
    private val getFinalDistance: GetFinalDistanceInteractor,
    private val getFinalAverageSpeed: GetFinalAverageSpeedInteractor,
    private val walkRepository: WalkRepository,
) {
    suspend operator fun invoke(context: Context, walkMapImage: Bitmap?): Result<Unit> {
        var walkMapImageName: String? = null

        if (walkMapImage != null) {
            walkMapImageName = "$BASE_IMAGE_NAME${Date().time}.png"
            imageManager.saveMapImage(context, walkMapImage, walkMapImageName)
        }

        val date = getWalkDate()
        val time = getFinalTime()
        val distance = getFinalDistance().toString()
        val speed = getFinalAverageSpeed().toString()
        val newWalk = NewWalk(walkMapImageName, date, time, distance, speed)

        return walkRepository.insertWalk(newWalk)
    }
}