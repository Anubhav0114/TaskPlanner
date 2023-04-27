package com.example.taskplanner.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateManager {

    fun unixMillToDateString(milliSec: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateTime = Date(milliSec)
        return dateFormat.format(dateTime)
    }
}