package com.example.trackerapp.utils

import com.example.trackerapp.data.entity.UserLocationData
import com.example.trackerapp.data.entity.WalkData
import com.example.trackerapp.entity.UserLocation
import com.example.trackerapp.entity.Walk
import com.google.android.gms.maps.model.LatLng

fun UserLocationData.toUserLocation() =
    UserLocation(LatLng(latitude, longitude), distance, time, speed)

fun UserLocation.toUserLocationData() =
    UserLocationData(
        latitude = location.latitude,
        longitude = location.longitude,
        distance = distance,
        time = time,
        speed = speed,
    )

fun List<UserLocationData>.toUserLocationList() = map {
    it.toUserLocation()
}

fun WalkData.toWalk() = Walk(mapImageName, date, time, distance, speed)

fun Walk.toWalkData() = WalkData(
    mapImageName = mapImageName,
    date = date,
    time = time,
    distance = distance,
    speed = speed,
)

fun List<WalkData>.toWalkList() = map {
    it.toWalk()
}