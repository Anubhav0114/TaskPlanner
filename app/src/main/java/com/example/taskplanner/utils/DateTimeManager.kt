package com.example.taskplanner.utils

import android.os.Build
import kotlinx.datetime.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DateTimeManager {


    fun localUnixMillisToUtcUnixMillis(localUnixMillis: Long): Long {
        val localInstant = Instant.fromEpochMilliseconds(localUnixMillis)
        return localInstant.toLocalDateTime(TimeZone.UTC).toInstant(TimeZone.UTC).toEpochMilliseconds()
    }

    fun currentTimeMillisecond(): Long {
        val currentTime = Clock.System.now()
        return currentTime.toEpochMilliseconds()
    }

    fun getTomorrowDate(): Long{
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val endOfDay = LocalDateTime(currentDate, LocalTime(23, 59, 59))
        return endOfDay.toInstant(TimeZone.UTC).toEpochMilliseconds()
    }



    fun parseDateOnly(unixTimestampMillis: Long): Long{
        val instant = Instant.fromEpochMilliseconds(unixTimestampMillis)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }

    fun parseTimeOnly(unixTimestampMillis: Long): Long{
        val instant = Instant.fromEpochMilliseconds(unixTimestampMillis)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        return (localDateTime.hour * 3600 + localDateTime.minute * 60 + localDateTime.second) * 1000L
    }



    fun unixMillToDateString(milliSec: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateTime = Date(milliSec)
        return dateFormat.format(dateTime)
    }

    fun unixMillToTimeString(milliSec: Long): String{
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateTime = Date(milliSec)
        return timeFormat.format(dateTime)
    }

    fun rawTimeToString(hours: Int, minutes: Int): String {
        var a = "AM"
        var cHours = hours
        if( hours > 12){
            cHours -= 12
            a = "PM"
        }

        return "${cHours}:${minutes} $a"
    }

    fun getCurrentHour(): Int {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    }

    fun getCurrentMin(): Int{
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).minute
    }

    fun getTimezoneOffset(): Long {
        val currentOffset = TimeZone.currentSystemDefault().offsetAt(Clock.System.now())
        return currentOffset.totalSeconds * 1000L
    }


    /*
    private val secondsInDay = 24 * 60 * 60             // seconds in one day
    private val secondsInWeek = secondsInDay * 7        // seconds in one week

    fun getDateWithin(dateTimeString: String): DateWithin{

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val now = LocalDateTime.now()
            val savedDateTime = LocalDateTime.parse(dateTimeString,
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
            val deltaSeconds = now.toEpochSecond(ZoneOffset.UTC) - savedDateTime.toEpochSecond(ZoneOffset.UTC)

            return if(deltaSeconds < secondsInDay){
                DateWithin.Today
            }else if(deltaSeconds < secondsInWeek){
                DateWithin.Week
            }else{
                DateWithin.Month
            }

        }else{
            val now = Calendar.getInstance()
            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val savedDateTime = formatter.parse(dateTimeString)
            val deltaSeconds = (now.time.time - savedDateTime!!.time) / 1000

            return if(deltaSeconds < secondsInDay){
                DateWithin.Today
            }else if(deltaSeconds < secondsInWeek){
                DateWithin.Week
            }else{
                DateWithin.Month
            }

        }
    }*/


}