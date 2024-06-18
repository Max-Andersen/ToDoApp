package com.toloknov.summerschool.todoapp.ui.utils

import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun Date.convertToReadable(): String = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale("ru")).format(this)

fun Date.convertToReadableTimeLess(): String = SimpleDateFormat("dd.MM.yyyy").format(this)


fun ZonedDateTime.convertToReadable(): String? {
    return this.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}

