package com.example.taskplanner.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.databinding.FragmentTaskBinding
import com.example.taskplanner.room.ProjectTask
import com.example.taskplanner.utils.DateTimeManager
import com.example.taskplanner.utils.TaskMode
import com.example.taskplanner.utils.TaskStatus
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.MaterialContainerTransform


class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding
    private var projectTask = ProjectTask(0,0,0,"","", false,0L,0L,"", TaskStatus.Active)
    private var startDateTime = 0L
    private var endDateTime = 0L
    private var taskMode = TaskMode.View
    private var isInProgress = false

    private val dateManager = DateTimeManager()

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(
            (requireActivity().application as ProjectApplication).projectRepository,
            (requireActivity().application as ProjectApplication).taskRepository
        )
    }
    private lateinit var contextApp: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 400
            scrimColor = Color.TRANSPARENT
        }

        // used to handle back press
        requireActivity().onBackPressedDispatcher.addCallback(this) {

            if(taskMode == TaskMode.Edit){

                // TODO add dialog to save edited data
                taskMode = TaskMode.View
                setupData()
            }else{
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }
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

        val arg = requireArguments()
        val isOpenTypeCreate = arg.getBoolean("isCreating", false)
        val taskId = arg.getLong("taskId")
        val projectId = arg.getLong("projectId")

        projectTask.taskId = taskId
        projectTask.projectId = projectId

        taskMode = if(isOpenTypeCreate){
            TaskMode.Create
        }else{
            TaskMode.View
        }

        setupData()
        setupListener()
    }


    private fun setupListener(){
        binding.startDateText.setOnClickListener {
            if(taskMode != TaskMode.View){
                openDatePicker(true)
            }
        }

        binding.endDateText.setOnClickListener {
            if(taskMode != TaskMode.View){
                openDatePicker(false)
            }
        }

        binding.startTimeText.setOnClickListener {
            if(taskMode != TaskMode.View){
                openTimePicker(true)
            }
        }

        binding.endTimeText.setOnClickListener {
            if(taskMode != TaskMode.View){
                openTimePicker(false)
            }
        }


        binding.saveTask.setOnClickListener {

            if(isInProgress) return@setOnClickListener

            // TODO check if everything is filled

            isInProgress = true

            projectTask.taskName = binding.titleText.text.toString()
            projectTask.description = binding.description.text.toString()
            projectTask.isRemind = binding.remindSwitch.isChecked
            projectTask.startTime = startDateTime
            projectTask.endTime = endDateTime

            if(projectTask.taskStatus == TaskStatus.Failed){
                projectTask.taskStatus = TaskStatus.Active
            }


            if(taskMode == TaskMode.Create){
                mainActivityViewModel.createProjectTask(projectTask){

                    // back to previous
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }else{
                mainActivityViewModel.updateProjectTask(projectTask){
                    taskMode = TaskMode.View
                    isInProgress = false
                    setupData()
                }
            }
        }

        binding.editTask.setOnClickListener {
            taskMode = TaskMode.Edit
            setupData()
        }

        binding.titleText.addTextChangedListener {
            projectTask.taskName = it!!.toString()
        }

        binding.description.addTextChangedListener {
            projectTask.description= it!!.toString()
        }
    }


    private fun setupData(){

        toggleEditOption()
        when (taskMode) {
            TaskMode.Create -> {
                binding.projectName.text = taskMode.toString()

            }
            TaskMode.Edit -> {
                binding.projectName.text = taskMode.toString()
                loadDataToUI()
            }
            else -> {
                val projectId = requireArguments().getLong("projectId")
                val taskId = requireArguments().getLong("taskId")
                binding.projectName.text = taskMode.toString()

                loadTaskData(projectId, taskId)
            }
        }
    }

    private fun loadDataToUI(){
        startDateTime = projectTask.startTime
        endDateTime = projectTask.endTime
        binding.titleText.setText(projectTask.taskName)
        binding.description.setText(projectTask.description)
        binding.startDateText.text = dateManager.unixMillToDateString(projectTask.startTime)
        binding.endDateText.text = dateManager.unixMillToDateString(projectTask.endTime)
        binding.remindSwitch.isChecked = projectTask.isRemind
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
        mainActivityViewModel.getProjectTaskById(projectId, taskId){
            projectTask = it
            loadDataToUI()
        }
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
//                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        datePicker.addOnPositiveButtonClickListener {milliSec ->
            if(isOpenedForStart){
                startDateTime = milliSec
                binding.startDateText.text = dateManager.unixMillToDateString(milliSec)
            }else{
                endDateTime = milliSec
                binding.endDateText.text = dateManager.unixMillToDateString(milliSec)
            }

        }
        datePicker.show(parentFragmentManager, "hello")

    }

    private fun openTimePicker(isOpenedForStart: Boolean){

    }
}