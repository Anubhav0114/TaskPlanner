package com.example.taskplanner.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
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
import com.example.taskplanner.utils.TaskStatus
import com.example.taskplanner.utils.generateUniqueId
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProjectFragment : Fragment() {

    private lateinit var binding: FragmentProjectBinding
    private lateinit var openedProject: Project
    private lateinit var projectTaskListAdapter: ProjectTaskListAdapter
    private lateinit var previousCheckedChip: CustomChip
    private lateinit var allTaskList: List<ProjectTask>
    private var projectId: Long = 0L

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
        binding = FragmentProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contextApp = requireContext()
        projectId = requireArguments().getLong("project_id")
        previousCheckedChip = binding.allChip
        setupData()
        loadData()
        addObserver()
        setupListener()

    }


    private fun loadData() {
        mainActivityViewModel.getProjectById(projectId) {
            openedProject = it
            binding.projectName.text = openedProject.projectName
            binding.notifyCheckbox.isChecked = openedProject.isNotify
            binding.pinCheckbox.isChecked = openedProject.isPinned
        }

    }

    private fun addObserver(){
        lifecycleScope.launch(Dispatchers.Default) {
            mainActivityViewModel.getAllTaskFromProject(projectId).collect { tasks ->
                allTaskList = tasks
                updateRecyclerView(tasks)
            }
        }
    }


    private fun updateRecyclerView(tasks: List<ProjectTask>){
        val filteredTask = ArrayList<ProjectTask>()
        when (previousCheckedChip.id) {
            R.id.allChip -> {
                filteredTask.addAll(tasks)
            }

            R.id.progressChip -> {
                for (task in tasks) {
                    if (task.taskStatus == TaskStatus.Active) filteredTask.add(task)
                }
            }

            R.id.doneChip -> {
                for (task in tasks) {
                    if (task.taskStatus == TaskStatus.Done) filteredTask.add(task)
                }
            }

            R.id.failedChip -> {
                for (task in tasks) {
                    if (task.taskStatus == TaskStatus.Failed) filteredTask.add(task)
                }
            }
        }
        projectTaskListAdapter.submitList(filteredTask)

        val allCount = tasks.size
        var activeCount = 0
        var doneCount = 0
        var failedCount = 0

        for (task in tasks) {
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
                override fun onItemClick(projectTask: ProjectTask) {
                    val bundle = Bundle().apply {
                        putBoolean("isCreating", false)
                        putLong("projectId", openedProject.projectId)
                        putLong("taskId", projectTask.taskId)
                    }
                    findNavController().navigate(
                        R.id.action_projectFragment_to_taskFragment,
                        bundle
                    )
                }

                override fun onCheckChangeListener(projectTask: ProjectTask) {
                    mainActivityViewModel.updateProjectTask(projectTask) {
                        Log.e("--------------", "Checked")
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
            val bundle = Bundle().apply {
                putBoolean("isCreating", true)
                putLong("projectId", openedProject.projectId)
                putLong("taskId", generateUniqueId())
            }
            findNavController().navigate(R.id.action_projectFragment_to_taskFragment, bundle)
        }
        binding.backBtn.setOnClickListener {}

        binding.allChip.setOnClickListener {
            if (previousCheckedChip == it as CustomChip) return@setOnClickListener
            it.setActive(true)
            previousCheckedChip.setActive(false)
            previousCheckedChip = it
            updateRecyclerView(allTaskList)
        }

        binding.progressChip.setOnClickListener {
            if (previousCheckedChip == it as CustomChip) return@setOnClickListener
            it.setActive(true)
            previousCheckedChip.setActive(false)
            previousCheckedChip = it
            updateRecyclerView(allTaskList)
        }

        binding.doneChip.setOnClickListener {
            if (previousCheckedChip == it as CustomChip) return@setOnClickListener
            it.setActive(true)
            previousCheckedChip.setActive(false)
            previousCheckedChip = it
            updateRecyclerView(allTaskList)
        }

        binding.failedChip.setOnClickListener {
            if (previousCheckedChip == it as CustomChip) return@setOnClickListener
            it.setActive(true)
            previousCheckedChip.setActive(false)
            previousCheckedChip = it
            updateRecyclerView(allTaskList)
        }

    }
}