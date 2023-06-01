package com.flaxstudio.taskplanner.room

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.flaxstudio.taskplanner.R

class NotificationManager {

    @RequiresApi(Build.VERSION_CODES.M)


    fun addNotification(contextApp: Context , currTask: ProjectTask){

     //   val currTask : Task = task as Task

        if (currTask.isRemind){

            val startTime = currTask.startTime
            val endTime = currTask.endTime
            val taskName = currTask.taskName
            val taskDescription = currTask.description
            val taskId = currTask.taskId
            val projectId = currTask.projectId


            // Create an intent for the Start time notification with a unique request code
            val startTimeIntent  = Intent(contextApp , StartTimeReceiver::class.java)
            startTimeIntent.putExtra("startTime" , startTime)
            startTimeIntent.putExtra("taskName" , taskName)
            startTimeIntent.putExtra("taskDescription" , taskDescription)
            startTimeIntent.putExtra("projectId" , projectId)
            val startTimePendingIntent = PendingIntent.getBroadcast(
                contextApp, currTask.projectId.hashCode(), startTimeIntent, PendingIntent.FLAG_IMMUTABLE
            )


            // Create an intent for the end time notification with a unique request code
            val endTimeIntent = Intent(contextApp, EndTimeReceiver::class.java)
            endTimeIntent.putExtra("endTime" , endTime)
            endTimeIntent.putExtra("taskName" , taskName)
            endTimeIntent.putExtra("taskDescription" , taskDescription)
            endTimeIntent.putExtra("taskId" , taskId)
            val endTimePendingIntent = PendingIntent.getBroadcast(
                contextApp, currTask.taskId.hashCode(), endTimeIntent, PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager1 = contextApp.getSystemService(Context.ALARM_SERVICE) as AlarmManager

             // Schedule the start time notification
            alarmManager1.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                currTask.startTime,
                startTimePendingIntent
            )

            // Schedule the end time notification
            alarmManager1.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                currTask.endTime,
                endTimePendingIntent
            )

        }else{

        }

       // startTimeIntent1.putExtra("taskDetail" , task)

    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun updateNotification(contextApp: Context, currTask: ProjectTask){

        //   val currTask : Task = task as Task

        val alarmManager1 = contextApp.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (currTask.isRemind){

            // This is to cancel the old pending intent
            val startIntent = Intent(contextApp , StartTimeReceiver::class.java)
            val pendingIntentStart = PendingIntent.getBroadcast(contextApp , currTask.projectId.hashCode() , startIntent , PendingIntent.FLAG_IMMUTABLE )
            alarmManager1.cancel(pendingIntentStart)

            val endIntent = Intent(contextApp , StartTimeReceiver::class.java)
            val pendingIntentEnd = PendingIntent.getBroadcast(contextApp , currTask.taskId.hashCode(), endIntent , PendingIntent.FLAG_IMMUTABLE )
            alarmManager1.cancel(pendingIntentEnd)


            // After cancelling the old pending intent will create a new pending intent with sane ids
            val startTime = currTask.startTime
            val endTime = currTask.endTime
            val taskName = currTask.taskName
            val taskDescription = currTask.description
            val taskId = currTask.taskId
            val projectId = currTask.projectId


            // Create an intent for the Start time notification with a unique request code
            val startTimeIntent  = Intent(contextApp , StartTimeReceiver::class.java)
            startTimeIntent.putExtra("startTime" , startTime)
            startTimeIntent.putExtra("taskName" , taskName)
            startTimeIntent.putExtra("taskDescription" , taskDescription)
            startTimeIntent.putExtra("projectId" , projectId)
            val startTimePendingIntent = PendingIntent.getBroadcast(
                contextApp, currTask.projectId.hashCode(), startTimeIntent, PendingIntent.FLAG_IMMUTABLE
            )


            // Create an intent for the end time notification with a unique request code
            val endTimeIntent = Intent(contextApp, EndTimeReceiver::class.java)
            endTimeIntent.putExtra("endTime" , endTime)
            endTimeIntent.putExtra("taskName" , taskName)
            endTimeIntent.putExtra("taskDescription" , taskDescription)
            endTimeIntent.putExtra("taskId" , taskId)
            val endTimePendingIntent = PendingIntent.getBroadcast(
                contextApp, currTask.taskId.hashCode(), endTimeIntent, PendingIntent.FLAG_IMMUTABLE
            )



            // Schedule the start time notification
            alarmManager1.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                currTask.startTime,
                startTimePendingIntent
            )

            // Schedule the end time notification
            alarmManager1.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                currTask.endTime,
                endTimePendingIntent
            )

        }else{

            // This code will cancel the pending intent if the is remind is false
            val startIntent = Intent(contextApp , StartTimeReceiver::class.java)
            val pendingIntentStart = PendingIntent.getBroadcast(contextApp , currTask.projectId.hashCode() , startIntent , PendingIntent.FLAG_IMMUTABLE )
            alarmManager1.cancel(pendingIntentStart)


            val endIntent = Intent(contextApp , StartTimeReceiver::class.java)
            val pendingIntentEnd = PendingIntent.getBroadcast(contextApp , currTask.taskId.hashCode(), endIntent , PendingIntent.FLAG_IMMUTABLE )
            alarmManager1.cancel(pendingIntentEnd)


        }

        // startTimeIntent1.putExtra("taskDetail" , task)

    }




//    @RequiresApi(Build.VERSION_CODES.M)
//    fun setupNotification(contextApp: Context){
//        // Alarm manager for notification
//        val taskOne : Task = Task(SystemClock.elapsedRealtime() + 60 * 1000, SystemClock.elapsedRealtime() + 120 * 1000 , "TaskOne" , "DescTaskOne" , 0)
////        var taskTwo : Task = Task(4484884, 5998595 , "TaskOne" , "DescTaskOne" , 44484)
////        var taskThree : Task = Task(4484884, 5998595 , "TaskOne" , "DescTaskOne" , 44484)
//
//        // List of tasks
//        val taskList = ArrayList<Task>()
//        taskList.add(taskOne)
//
//
//        // Loop through the task list and schedule notifications for each task
//        val alarmManager1 = contextApp.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        for (task in taskList) {
//            // Create an intent for the start time notification with a unique request code
//            val startTimeIntent = Intent(contextApp, StartTimeReceiver::class.java)
//            val startTimePendingIntent = PendingIntent.getBroadcast(
//                contextApp, 0, startTimeIntent, PendingIntent.FLAG_IMMUTABLE
//            )
//
//            // Create an intent for the end time notification with a unique request code
//            val endTimeIntent = Intent(contextApp, EndTimeReceiver::class.java)
//            val endTimePendingIntent = PendingIntent.getBroadcast(
//                contextApp, 123, endTimeIntent, PendingIntent.FLAG_IMMUTABLE
//            )
//
//            // Schedule the start time notification
//            alarmManager1.setExactAndAllowWhileIdle(
//                AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                task.startTime,
//                startTimePendingIntent
//            )
//
//            // Schedule the end time notification
//            alarmManager1.setExactAndAllowWhileIdle(
//                AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                task.endTime,
//                endTimePendingIntent
//            )
//        }
//
//    }

    inner class StartTimeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Show notification for start time
            val channelId = "CHANNEL_ID"

            val startTime = intent.getStringExtra("startTime")
            val name = intent.getStringExtra("taskName")
            val description = intent.getStringExtra("taskDescription")
            val id = intent.getStringExtra("projectId")

            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setContentTitle(name)
                .setContentText(description)
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
            notificationManager.notify(id.hashCode(), notificationBuilder.build())

        }
    }



    inner class EndTimeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Show notification for end time

            val name = intent.getStringExtra("taskName")
            val description = intent.getStringExtra("taskDescription")
            val id = intent.getStringExtra("taskId")

            val channelId = "CHANNEL_ID"
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setContentTitle(name)
                .setContentText(description)
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
            notificationManager.notify(id.hashCode(), notificationBuilder.build())

        }
    }

}