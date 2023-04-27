package com.example.taskplanner.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.databinding.FragmentTaskBinding
import com.example.taskplanner.room.ProjectTask
import com.example.taskplanner.utils.TaskMode
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import java.util.Date


class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding
    private val projectTask = ProjectTask(0,0,0,"","", false,0L,0L,"", "")
    private var taskMode = TaskMode.View

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
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contextApp = requireContext()

        val isOpenTypeCreate = requireArguments().getBoolean("isCreating", false)
        taskMode = if(isOpenTypeCreate){
            TaskMode.Create
        }else{
            TaskMode.View
        }

        setupData()
        setupListener()
    }


    private fun setupListener(){
        binding.startTime.setOnClickListener {
            if(taskMode != TaskMode.View){
                openDatePicker(true)
            }

        }

        binding.endTime.setOnClickListener {
            if(taskMode != TaskMode.View){
                openDatePicker(false)
            }
        }
    }


    private fun setupData(){

        toggleEditOption()
        if(taskMode == TaskMode.Create || taskMode == TaskMode.Edit){
            binding.projectName.text = taskMode.toString()

        }else{
            val projectId = requireArguments().getLong("projectId")
            val taskId = requireArguments().getLong("taskId")
            binding.projectName.text = taskMode.toString()

            loadTaskData(projectId, taskId)
        }
    }

    private fun toggleEditOption(){
        if(taskMode == TaskMode.Create || taskMode == TaskMode.Edit){
            binding.titleText.isEnabled = true
            binding.description.isEnabled = true
            binding.remindSwitch.isEnabled = true

            binding.editTask.visibility = View.INVISIBLE
            binding.deleteTask.visibility = View.INVISIBLE
            binding.saveTask.visibility = View.VISIBLE
        }else{
            binding.titleText.isEnabled = false
            binding.description.isEnabled = false
            binding.remindSwitch.isEnabled = false

            binding.editTask.visibility = View.VISIBLE
            binding.deleteTask.visibility = View.VISIBLE
            binding.saveTask.visibility = View.INVISIBLE
        }
    }

    private fun loadTaskData(projectId: Long, taskId: Long){

    }

    private fun openDatePicker(isOpenedForStart: Boolean){
        // Makes only dates from today forward selectable.
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        datePicker.addOnPositiveButtonClickListener {milliSec ->
            if(isOpenedForStart){
                projectTask.startTime = milliSec
            }else{
                projectTask.endTime = milliSec
            }

            val date = Date(milliSec)
            Log.e("=========", date.toString())
        }
        datePicker.show(parentFragmentManager, "hello")

    }
}