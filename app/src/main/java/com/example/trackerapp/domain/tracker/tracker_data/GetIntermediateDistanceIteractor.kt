package com.example.trackerapp.domain.tracker.tracker_data

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import javax.inject.Inject
import kotlin.math.roundToInt

class GetIntermediateDistanceIteractor @Inject constructor(
    private val getLastUserLocation: GetLastUserLocationInteractor,
) {
    suspend operator fun invoke(currentUserLocation: LatLng): Double {
        val lastUserLocation = getLastUserLocation()?.location ?: currentUserLocation
        val distance = SphericalUtil.computeDistanceBetween(lastUserLocation, currentUserLocation)
        return (distance * 10.0).roundToInt() / 10.0
    }
}