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
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.databinding.FragmentProjectBinding
import com.example.taskplanner.room.Project
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.material.transition.MaterialContainerTransform


class ProjectFragment : Fragment() {

    private lateinit var binding: FragmentProjectBinding
    private lateinit var openedProject: Project

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
        Log.e("=============", projectId.toString())
        setupData(projectId)


        binding.createTask.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("isCreating", true)
            }
            findNavController().navigate(R.id.action_projectFragment_to_taskFragment, bundle)
        }
        binding.backBtn.setOnClickListener {}
    }


    private fun setupData(projectId: Long){
        mainActivityViewModel.getProjectById(projectId){
            openedProject = it
            binding.projectName.text = it.projectName
            binding.notifyCheckbox.isChecked = it.isNotify
            binding.pinCheckbox.isChecked = it.isPinned
        }

    }
}