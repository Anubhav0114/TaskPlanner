package com.example.taskplanner.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

class DateTimeManager {


    fun currentTimeMillisecond(): Long {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val current = LocalDateTime.now()
            current.toEpochSecond(ZoneOffset.UTC) * 1000L
        }else{
            val calendar = Calendar.getInstance()
            calendar.timeInMillis
        }
    }

    fun getTomorrowDate(): Long{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val tomorrow = LocalDate.now().plusDays(1)
            val startOfDay = tomorrow.atStartOfDay().toInstant(ZoneOffset.UTC)
            startOfDay.toEpochMilli()
        }else{
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 1) // Add 1 day to get tomorrow's date
            calendar.set(Calendar.HOUR_OF_DAY, 0) // Set the time to 00:00:00
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }
    }


    fun parseDateOnly(unixTimestampMillis: Long): Long{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            // Convert Unix timestamp to LocalDateTime
            val dateTime = Instant.ofEpochMilli(unixTimestampMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

            // Extract the date portion from the LocalDateTime
            val date = dateTime.toLocalDate()
            // Convert the date to milliseconds
            val dateInMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            dateInMillis
        }else{
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.timeInMillis = unixTimestampMillis

            // Clear the time portion of the Calendar
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val date = calendar.time
            date.time
        }
    }

    fun parseTimeOnly(unixTimestampMillis: Long): Long{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Convert Unix timestamp to LocalDateTime
            val dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(unixTimestampMillis),
                ZoneId.systemDefault()
            )

            // Extract the time portion from the LocalDateTime
            val time = dateTime.toLocalTime()
            time.toNanoOfDay() / 1000

        }else {

            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.timeInMillis = unixTimestampMillis

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)
            val millisecond = calendar.get(Calendar.MILLISECOND)
            (hour * 60L * 60L * 1000L) + (minute * 60L * 1000L) + (second * 1000L) + millisecond
        }
    }



    fun unixMillToDateString(milliSec: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateTime = Date(milliSec)
        return dateFormat.format(dateTime)
    }

    fun unixMillToTimeString(milliSec: Long): String{
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSec
        return sdf.format(calendar.time).uppercase()
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
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val now = LocalDateTime.now()
            now.hour
        }else{
            val now = Calendar.getInstance()
            now.get(Calendar.HOUR_OF_DAY)
        }
    }

    fun getCurrentMin(): Int{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val now = LocalDateTime.now()
            now.minute
        }else{
            val now = Calendar.getInstance()
            now.get(Calendar.MINUTE)
        }
    }

    fun getTimezoneOffset(): Long {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            OffsetDateTime.now().offset.totalSeconds * 1000L
        }else{
            TimeZone.getDefault().rawOffset.toLong()
        }
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