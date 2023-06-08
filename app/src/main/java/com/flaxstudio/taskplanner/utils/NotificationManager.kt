package com.flaxstudio.taskplanner.utils

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
import androidx.core.content.ContextCompat
import com.flaxstudio.taskplanner.R
import com.flaxstudio.taskplanner.room.ProjectTask
import com.flaxstudio.taskplanner.room.Task

private const val TAG = "NotificationManager"

class NotificationManager {


    @RequiresApi(Build.VERSION_CODES.M)
    fun addNotification(contextApp: Context , currTask: ProjectTask) {

        //  This is the dummy data for testing don't remove it without Author's permission
        val currTask1 = Task(
            3,
            453,
            4532,
            "Test Notification",
            "Desc of test notification",
            true,
            SystemClock.elapsedRealtime() + 60 * 1000,
            SystemClock.elapsedRealtime() + 120 * 1000,
            " d",
            TaskStatus.Active
        )
        if (currTask.isRemind && currTask.taskStatus == TaskStatus.Active) {

            val startTime = currTask.startTime
            val endTime = currTask.endTime
            val taskName = currTask.taskName
            val taskDescription = currTask.description
            val taskId = currTask.taskId
            val projectId = currTask.projectId


            Log.i(TAG, " Creating Start Time Intent")
            // Create an intent for the Start time notification with a unique request code
            val startTimeIntent = Intent(contextApp, StartTimeReceiver::class.java)
            startTimeIntent.putExtra("startTime", startTime)
            startTimeIntent.putExtra("taskName", taskName)
            startTimeIntent.putExtra("taskDescription", taskDescription)
            startTimeIntent.putExtra("projectId", projectId)
            val startTimePendingIntent = PendingIntent.getBroadcast(
                contextApp,
                projectId.hashCode(),
                startTimeIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            Log.i(TAG, " Creating End Time Intent")

            // Create an intent for the end time notification with a unique request code
            val endTimeIntent = Intent(contextApp, EndTimeReceiver::class.java)
            endTimeIntent.putExtra("endTime", endTime)
            endTimeIntent.putExtra("taskName", taskName)
            endTimeIntent.putExtra("taskDescription", taskDescription)
            endTimeIntent.putExtra("taskId", taskId)
            val endTimePendingIntent = PendingIntent.getBroadcast(
                contextApp, taskId.hashCode(), endTimeIntent, PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager1 = contextApp.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // val alarmManager = ContextCompat.getSystemService(contextApp, AlarmManager::class.java) as AlarmManager

            // Schedule the start time notification
            alarmManager1.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 180 * 1000,
                //currTask.startTime,
                startTimePendingIntent
            )

            // Schedule the end time notification
            alarmManager1.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 200 * 1000,
                // currTask.endTime,
                endTimePendingIntent
            )

        } else {

        }

        // startTimeIntent1.putExtra("taskDetail" , task)

    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun updateNotification(contextApp: Context, currTask: ProjectTask) {

        //   val currTask : Task = task as Task

        val alarmManager1 = contextApp.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (currTask.isRemind && currTask.taskStatus == TaskStatus.Active) {

            // This is to cancel the old pending intent
            val startIntent = Intent(contextApp, StartTimeReceiver::class.java)
            val pendingIntentStart = PendingIntent.getBroadcast(
                contextApp,
                currTask.projectId.hashCode(),
                startIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager1.cancel(pendingIntentStart)
            Log.i(TAG ,"Removing the old start Time Notification")

            val endIntent = Intent(contextApp, EndTimeReceiver::class.java)
            val pendingIntentEnd = PendingIntent.getBroadcast(
                contextApp,
                currTask.taskId.hashCode(),
                endIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            Log.i(TAG ,"Removing the old End Time Notification")
            alarmManager1.cancel(pendingIntentEnd)


             addNotification(contextApp , currTask)


        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun addGroupNotification(contextApp: Context, taskList : List<ProjectTask>){
        for (task in taskList){
            addNotification(contextApp , task)
        }
    }

}

class StartTimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Show notification for start time
        val channelId = "CHANNEL_ID"

        val name = intent.getStringExtra("taskName")
        val description = intent.getStringExtra("taskDescription")
        //  val id = intent.getExtra("projectId")
        val id = intent.getLongExtra("projectId", 0)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(name)
            .setContentText(description)
            .setSmallIcon(R.drawable.icon_task_done)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        Log.i(TAG, "This is the notification Builder class")


        // Show the notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(id.hashCode(), notificationBuilder.build())

    }
}


class EndTimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Show notification for end time

        val name = intent.getStringExtra("taskName")
        val description = intent.getStringExtra("taskDescription")
        // val id = intent.getStringExtra("taskId")
        val id = intent.getLongExtra("taskId", 0)


        val channelId = "CHANNEL_ID"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(name)
            .setContentText(description)
            .setSmallIcon(R.drawable.icon_not_finished)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        Log.i(TAG, "This is the notification Builder End time class")


// Show the notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(id.hashCode(), notificationBuilder.build())

    }
}

