package com.example.trackerapp.data.typeconverters

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class LocalDateTimeConverter {
    @TypeConverter
    fun fromTimestamp(time: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time),
            TimeZone.getDefault().toZoneId())
    }

    @TypeConverter
    fun toTimestamp(time: LocalDateTime): Long {
        val zdt = ZonedDateTime.of(time, ZoneId.systemDefault())
        return zdt.toInstant().toEpochMilli()
    }
}