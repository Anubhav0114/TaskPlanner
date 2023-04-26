package com.example.taskplanner.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.databinding.FragmentTaskBinding
import com.example.taskplanner.room.ProjectTask
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory


class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding
    private var isOpenTypeCreate: Boolean = false
    private lateinit var projectTask: ProjectTask

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

//        isOpenTypeCreate = requireArguments().getBoolean("isCreating", false)
//
//        setupData()
    }


    private fun setupData(){

        if(isOpenTypeCreate){
            binding.projectName.text = "Create"
        }else{
            val projectId = requireArguments().getLong("projectId")
            val taskId = requireArguments().getLong("taskId")
            binding.projectName.text = "View"

            loadTaskData(projectId, taskId)
        }
    }

    private fun loadTaskData(projectId: Long, taskId: Long){

    }
}