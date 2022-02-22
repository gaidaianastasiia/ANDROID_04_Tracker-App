package com.example.trackerapp.entity

data class Walk(
    val id: Long,
    val mapImageName: String?,
    val date: String,
    val time: String,
    val distance: String,
    val speed: String,
)
