package com.vereskul.tc51versusxml.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


class Converters {
    @TypeConverter
    fun fromDate(date:LocalDateTime?):Long?{
        val zone = ZoneId.of("Europe/Kiev")
        val zoneOffSet = zone.rules.getOffset(LocalDateTime.now());
        return date?.toEpochSecond(zoneOffSet)
    }
    @TypeConverter
    fun toDate(date: Long?):LocalDateTime?{
        date?.let { return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(it),
            ZoneId.of("Europe/Kiev")
        ) }
        return null
    }
}