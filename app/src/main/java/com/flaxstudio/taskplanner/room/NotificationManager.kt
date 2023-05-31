package com.flaxstudio.taskplanner.room

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.taskplanner.R
import com.example.taskplanner.Task

class NotificationManager {

    @RequiresApi(Build.VERSION_CODES.M)
    fun setupNotification(contextApp: Context){
        // Alarm manager for notification
        val taskOne : Task = Task(SystemClock.elapsedRealtime() + 60 * 1000, SystemClock.elapsedRealtime() + 120 * 1000 , "TaskOne" , "DescTaskOne" , 0)
//        var taskTwo : Task = Task(4484884, 5998595 , "TaskOne" , "DescTaskOne" , 44484)
//        var taskThree : Task = Task(4484884, 5998595 , "TaskOne" , "DescTaskOne" , 44484)

        // List of tasks
        val taskList = ArrayList<Task>()
        taskList.add(taskOne)


        // Loop through the task list and schedule notifications for each task
        val alarmManager1 = contextApp.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (task in taskList) {
            // Create an intent for the start time notification with a unique request code
            val startTimeIntent = Intent(contextApp, StartTimeReceiver::class.java)
            val startTimePendingIntent = PendingIntent.getBroadcast(
                contextApp, 0, startTimeIntent, PendingIntent.FLAG_IMMUTABLE
            )

            // Create an intent for the end time notification with a unique request code
            val endTimeIntent = Intent(contextApp, EndTimeReceiver::class.java)
            val endTimePendingIntent = PendingIntent.getBroadcast(
                contextApp, 123, endTimeIntent, PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule the start time notification
            alarmManager1.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                task.startTime,
                startTimePendingIntent
            )

            // Schedule the end time notification
            alarmManager1.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                task.endTime,
                endTimePendingIntent
            )
        }

    }

    inner class StartTimeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Show notification for start time
            val channelId = "CHANNEL_ID"

            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Task Started")
                .setContentText("Your task has started now")
                .setSmallIcon(R.drawable.icon_task_done)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            Log.i("HomeFragment" , "This is the notification Builder class")


            // Show the notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(0, notificationBuilder.build())

        }
    }



    inner class EndTimeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Show notification for end time

            val channelId = "CHANNEL_ID"
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setContentTitle("Task Ended")
                .setContentText("Did you finished your task")
                .setSmallIcon(R.drawable.icon_not_finished)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)


// Show the notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(123, notificationBuilder.build())

        }
    }

}