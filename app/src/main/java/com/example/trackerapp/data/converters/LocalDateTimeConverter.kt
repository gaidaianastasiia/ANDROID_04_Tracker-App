package com.example.trackerapp.data.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.util.*

class LocalDateTimeConverter {
    @TypeConverter
    fun fromTimestamp(time: String): LocalDateTime = LocalDateTime.parse(time)

    @TypeConverter
    fun toTimestamp(time: LocalDateTime): String = time.toString()
}