package com.toloknov.summerschool.database.utils

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object DbConverter {
    @TypeConverter
    @JvmStatic
    fun zonedDateTimeToLong(date: ZonedDateTime?) = date?.toEpochSecond()

    @TypeConverter
    @JvmStatic
    fun longToZonedDateTime(long: Long?) = if (long != null) ZonedDateTime.ofInstant(
        Instant.ofEpochSecond(long),
        ZoneId.systemDefault()
    ) else null
}