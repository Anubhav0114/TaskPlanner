package com.example.taskplanner.fragments


import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskplanner.MainActivity
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.Task
import com.example.taskplanner.adapters.HomeChipListAdapter
import com.example.taskplanner.adapters.HomeProjectListAdapter
import com.example.taskplanner.databinding.FragmentHomeBinding
import com.example.taskplanner.utils.ChipData
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(
            (requireActivity().application as ProjectApplication).projectRepository,
            (requireActivity().application as ProjectApplication).taskRepository
        )
    }
    private lateinit var contextApp: Context


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contextApp = requireContext()

        setupData()

        binding.navButton.setOnClickListener{
            (activity as MainActivity).openNavDrawer()
        }

        binding.newProjectBtn.setOnClickListener {
            // Alert Box
            dialog()
        }
        //findNavController().navigate(R.id.action_homeFragment_to_projectFragment)



        // Alarm manager for notification
        var taskOne : Task = Task(SystemClock.elapsedRealtime() + 60 * 1000, SystemClock.elapsedRealtime() + 120 * 1000 , "TaskOne" , "DescTaskOne" , 0)
//        var taskTwo : Task = Task(4484884, 5998595 , "TaskOne" , "DescTaskOne" , 44484)
//        var taskThree : Task = Task(4484884, 5998595 , "TaskOne" , "DescTaskOne" , 44484)

        // List of tasks
        val taskList = ArrayList<Task>()
        taskList.add(taskOne)


        // Loop through the task list and schedule notifications for each task
        val alarmManager1 = contextApp?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
            alarmManager1?.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                task.startTime,
                startTimePendingIntent
            )

            // Schedule the end time notification
            alarmManager1?.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                task.endTime,
                endTimePendingIntent
            )
        }









//        val alarmManager = contextApp?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        val intent =  Intent(contextApp, StartTimeReceiver::class.java)
//        Log.i("HomeFragment" , "Setting the pending intent")
//        val pendingIntent : PendingIntent = PendingIntent.getBroadcast(
//            requireContext(), 0, intent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//
//
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.ELAPSED_REALTIME_WAKEUP,
//            SystemClock.elapsedRealtime() + 60 * 1000,
//            pendingIntent
//        )








    }

    private fun dialog(){
        val dialogView = LayoutInflater.from(contextApp).inflate(R.layout.add_project_dialog, null)
        val builder = AlertDialog.Builder(contextApp)
        builder.setView(dialogView)

        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.dialog_create)?.setOnClickListener {

            val name = dialogView.findViewById<EditText>(R.id.dialog_input).text.toString().trim().replace("\\s+", " ")
            if(name.isBlank()){
                Toast.makeText(contextApp, "Name must not be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mainActivityViewModel.createNewProject(name){
                dialog.dismiss()
                Toast.makeText(contextApp, "Project created", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<Button>(R.id.dialog_cancel)?.setOnClickListener {
            // handle Cancel button click
            Toast.makeText(contextApp,"Close It",Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialog.show()

    }


    private fun setupData(){
        setupRecentRecyclerView()
    }


    private lateinit var chipListAdapter: HomeChipListAdapter
    private lateinit var projectListAdapter: HomeProjectListAdapter
    private fun setupRecentRecyclerView(){
        // 1
        // setup data in chips
        chipListAdapter = HomeChipListAdapter()
        binding.homeChipRecyclerview.adapter = chipListAdapter
        val chipLayoutManager = LinearLayoutManager(contextApp, LinearLayoutManager.HORIZONTAL, false)
        binding.homeChipRecyclerview.layoutManager = chipLayoutManager

        val chipList = arrayListOf<ChipData>()
        chipList.add(ChipData("All", 5, true))
        chipList.add(ChipData("Android", 10, false))
        chipList.add(ChipData("Game", 17, false))
        chipList.add(ChipData("", 0, false))
        chipListAdapter.submitList(chipList)


        // 2
        // setup data in project adapter
        projectListAdapter = HomeProjectListAdapter()
        val projectLayoutManager = LinearLayoutManager(contextApp)
        binding.allProjectRecyclerView.adapter = projectListAdapter
        binding.allProjectRecyclerView.layoutManager = projectLayoutManager

        binding.allProjectRecyclerView.addItemDecoration(
            DividerItemDecoration(contextApp, projectLayoutManager.orientation)
        )

        mainActivityViewModel.getAllProjectsTask {
            projectListAdapter.submitList(it)
        }

    }

}

class StartTimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Show notification for start time
        var channelId = "CHANNEL_ID"

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



class EndTimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Show notification for end time

        var channelId = "CHANNEL_ID"
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

