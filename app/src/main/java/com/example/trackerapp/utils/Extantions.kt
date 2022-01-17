package com.example.trackerapp.utils

import com.example.trackerapp.data.entity.UserLocationData
import com.example.trackerapp.entity.UserLocation
import com.google.android.gms.maps.model.LatLng

fun UserLocationData.toUserLocation() = UserLocation(LatLng(latitude, longitude), distance, time, speed)

fun List<UserLocationData>.toUserLocationList() = map {
    it.toUserLocation()
}