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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.adapters.ProjectTaskListAdapter
import com.example.taskplanner.databinding.FragmentProjectBinding
import com.example.taskplanner.room.Project
import com.example.taskplanner.room.ProjectTask
import com.example.taskplanner.utils.generateUniqueId
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.material.transition.MaterialContainerTransform


class ProjectFragment : Fragment() {

    private lateinit var binding: FragmentProjectBinding
    private lateinit var openedProject: Project
    private lateinit var projectTaskListAdapter: ProjectTaskListAdapter

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

        val projectId = requireArguments().getLong("project_id")
        setupData()
        loadData(projectId)
        setupListener()

    }


    private fun loadData(projectId: Long){
        mainActivityViewModel.getProjectById(projectId){
            openedProject = it
            binding.projectName.text = openedProject.projectName
            binding.notifyCheckbox.isChecked = openedProject.isNotify
            binding.pinCheckbox.isChecked = openedProject.isPinned
        }

        mainActivityViewModel.getAllTaskFromProject(projectId){ tasks ->
            projectTaskListAdapter.submitList(tasks)
        }
    }

    private fun setupData(){

        // setting up recyclerview
        projectTaskListAdapter = ProjectTaskListAdapter(object : ProjectTaskListAdapter.OnItemClickListener{
            override fun onItemClick(project: ProjectTask) {

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


    private fun setupListener(){
        binding.createTask.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("isCreating", true)
                putLong("projectId", openedProject.projectId)
                putLong("taskId", generateUniqueId())
            }
            findNavController().navigate(R.id.action_projectFragment_to_taskFragment, bundle)
        }
        binding.backBtn.setOnClickListener {}
    }
}