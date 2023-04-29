package com.example.taskplanner.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.databinding.FragmentPinnedProjectBinding
import com.example.taskplanner.room.Project
import com.example.taskplanner.utils.TaskStatus
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import kotlinx.coroutines.launch

class PinnedProjectFragment(private val isDaily: Boolean, private val project: Project) : Fragment() {

    private lateinit var binding: FragmentPinnedProjectBinding
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
        binding = FragmentPinnedProjectBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contextApp = requireContext()

        Log.e("-----------------", "viewpager child added")
        setupUI()
        addObservers()
    }


    private fun setupUI(){
        binding.projectName.text = project.projectName
    }
    private fun addObservers(){

        if(!isDaily){
            lifecycleScope.launch{
                mainActivityViewModel.getAllTaskFromProject(project.projectId).collect{
                    var taskDone = 0
                    for(task in it){
                        if(task.taskStatus != TaskStatus.Active){
                            taskDone++
                        }
                    }

                    binding.taskDone.text = "$taskDone/${it.size}"
                }
            }
        }
    }

}