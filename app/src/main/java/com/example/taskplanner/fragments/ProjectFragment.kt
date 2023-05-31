package com.example.taskplanner.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.adapters.ProjectTaskListAdapter
import com.example.taskplanner.customview.CustomChip
import com.example.taskplanner.databinding.FragmentProjectBinding
import com.example.taskplanner.room.Project
import com.example.taskplanner.room.ProjectTask
import com.example.taskplanner.utils.DateTimeManager
import com.example.taskplanner.utils.TaskMode
import com.example.taskplanner.utils.TaskStatus
import com.example.taskplanner.utils.generateUniqueId
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProjectFragment : Fragment() {

    private lateinit var binding: FragmentProjectBinding
    private lateinit var openedProject: Project
    private lateinit var projectTaskListAdapter: ProjectTaskListAdapter
    private lateinit var previousCheckedChip: CustomChip
    private lateinit var allTaskList: List<ProjectTask>
    private var projectId: Long = 0L

    private val dateTimeManager = DateTimeManager()

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
            isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // handle animations
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        contextApp = requireContext()
        projectId = requireArguments().getLong("project_id")
        previousCheckedChip = binding.progressChip
        setupData()
        loadData()
        addObserver()
        setupListener()

    }


    private fun loadData() {

        if(projectId == 100L){          // for today task only
            binding.projectName.text = "Today Task"
            binding.notifyCheckbox.visibility = View.INVISIBLE
            binding.pinCheckbox.visibility = View.INVISIBLE
            binding.createTask.visibility = View.GONE

        }else{
            mainActivityViewModel.getProjectById(projectId) {
                openedProject = it
                binding.projectName.text = openedProject.projectName
                binding.notifyCheckbox.isChecked = openedProject.isNotify
                binding.pinCheckbox.isChecked = openedProject.isPinned
            }
        }


    }

    private fun addObserver(){

        if(projectId == 100L){         // only for today task
            lifecycleScope.launch(Dispatchers.Default){
                mainActivityViewModel.getAllTodayTasks(dateTimeManager.getTomorrowDate()).collect {
                    allTaskList = it
                    withContext(Dispatchers.Main){
                        updateRecyclerView()
                    }
                }
            }

        }else{
            lifecycleScope.launch(Dispatchers.Default) {
                mainActivityViewModel.getAllTaskFromProject(projectId).collect {
                    allTaskList = it
                    withContext(Dispatchers.Main){
                        updateRecyclerView()
                    }
                }
            }
        }

    }


    private fun updateRecyclerView(){
        val filteredTask = ArrayList<ProjectTask>()
        when (previousCheckedChip.id) {
            R.id.allChip -> {
                filteredTask.addAll(allTaskList)
            }

            R.id.progressChip -> {
                for (task in allTaskList) {
                    if (task.taskStatus == TaskStatus.Active) filteredTask.add(task)
                }
            }

            R.id.doneChip -> {
                for (task in allTaskList) {
                    if (task.taskStatus == TaskStatus.Done) filteredTask.add(task)
                }
            }

            R.id.failedChip -> {
                for (task in allTaskList) {
                    if (task.taskStatus == TaskStatus.Failed) filteredTask.add(task)
                }
            }
        }
        projectTaskListAdapter.submitList(filteredTask)

        val allCount = allTaskList.size
        var activeCount = 0
        var doneCount = 0
        var failedCount = 0

        for (task in allTaskList) {
            if (task.taskStatus == TaskStatus.Active) {
                activeCount++
                continue
            }
            if (task.taskStatus == TaskStatus.Done) {
                doneCount++
                continue
            }
            if (task.taskStatus == TaskStatus.Failed) {
                failedCount++
                continue
            }
        }

        binding.allChip.setInfoText(allCount.toString())
        binding.progressChip.setInfoText(activeCount.toString())
        binding.doneChip.setInfoText(doneCount.toString())
        binding.failedChip.setInfoText(failedCount.toString())

    }


    private fun setupData() {

        // setting up recyclerview
        projectTaskListAdapter =
            ProjectTaskListAdapter(object : ProjectTaskListAdapter.OnItemClickListener {
                override fun onItemClick(projectTask: ProjectTask, view: View) {

                    exitTransition = MaterialElevationScale(false).apply {
                        duration = 400
                    }
                    reenterTransition = MaterialElevationScale(true).apply {
                        duration = 400
                    }

                    val extras = FragmentNavigatorExtras(view to "task_fragment")

                    val bundle = Bundle().apply {
                        putBoolean("isCreating", false)
                        putLong("projectId", projectId )
                        putLong("taskId", projectTask.taskId)
                    }
                    findNavController().navigate(
                        R.id.action_projectFragment_to_taskFragment,
                        bundle,
                        null,
                        extras
                    )
                }

                override fun onCheckChangeListener(projectTask: ProjectTask) {

                    mainActivityViewModel.updateProjectTask(projectTask) {
                        mainActivityViewModel.updateProjectProgress(projectId)
                    }
                }
            })

        val taskLinearLayoutManager = LinearLayoutManager(contextApp)
        binding.taskRecyclerview.apply {
            adapter = projectTaskListAdapter
            layoutManager = taskLinearLayoutManager
            addItemDecoration(
                DividerItemDecoration(contextApp, taskLinearLayoutManager.orientation)
            )
        }

    }


    private fun setupListener() {
        binding.createTask.setOnClickListener {

            exitTransition = MaterialElevationScale(false).apply {
                duration = 1000
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = 1000
            }

            val extras = FragmentNavigatorExtras(it to "task_fragment")

            val id = generateUniqueId()
            it.transitionName = id.toString()

            val bundle = Bundle().apply {
                putBoolean("isCreating", true)
                putLong("projectId", openedProject.projectId)
                putLong("taskId", id)
            }
            findNavController().navigate(
                R.id.action_projectFragment_to_taskFragment,
                bundle,
                null,
                extras
            )

        }

        binding.backBtn.setOnClickListener {
            // back to previous
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.pinCheckbox.setOnCheckedChangeListener { _, isChecked ->
            openedProject.isPinned = isChecked
            mainActivityViewModel.updateProject(openedProject)
        }

        binding.notifyCheckbox.setOnCheckedChangeListener { _, isChecked ->
            openedProject.isNotify = isChecked
            mainActivityViewModel.updateProject(openedProject)
        }

        binding.allChip.setOnClickListener {
            if (previousCheckedChip == it as CustomChip) return@setOnClickListener
            it.setActive(true)
            previousCheckedChip.setActive(false)
            previousCheckedChip = it
            updateRecyclerView()
        }

        binding.progressChip.setOnClickListener {
            if (previousCheckedChip == it as CustomChip) return@setOnClickListener
            it.setActive(true)
            previousCheckedChip.setActive(false)
            previousCheckedChip = it
            updateRecyclerView()
        }

        binding.doneChip.setOnClickListener {
            if (previousCheckedChip == it as CustomChip) return@setOnClickListener
            it.setActive(true)
            previousCheckedChip.setActive(false)
            previousCheckedChip = it
            updateRecyclerView()
        }

        binding.failedChip.setOnClickListener {
            if (previousCheckedChip == it as CustomChip) return@setOnClickListener
            it.setActive(true)
            previousCheckedChip.setActive(false)
            previousCheckedChip = it
            updateRecyclerView()
        }

    }
}