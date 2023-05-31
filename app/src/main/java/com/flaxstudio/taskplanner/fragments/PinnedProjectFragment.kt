package com.flaxstudio.taskplanner.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flaxstudio.taskplanner.ProjectApplication
import com.flaxstudio.taskplanner.R
import com.flaxstudio.taskplanner.databinding.FragmentPinnedProjectBinding
import com.flaxstudio.taskplanner.room.Project
import com.flaxstudio.taskplanner.utils.DateTimeManager
import com.flaxstudio.taskplanner.utils.TaskStatus
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModel
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModelFactory
import kotlinx.coroutines.launch

class PinnedProjectFragment(private val isDaily: Boolean, private val project: Project) : Fragment() {

    private val dateTimeManager = DateTimeManager()

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
        setupListeners()
        addObservers()
    }


    private fun setupListeners(){
        binding.pinnedProject.setOnClickListener {

//            if(project.projectName == "Today Task") return@setOnClickListener

            val bundle = Bundle().apply {
                putLong("project_id", project.projectId)
            }

            findNavController().navigate(
                R.id.action_homeFragment_to_projectFragment,
                bundle,
                null
            )
        }
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
        }else{
            lifecycleScope.launch {
                mainActivityViewModel.getAllTodayTasks(dateTimeManager.getTomorrowDate()).collect {
                    var taskDone = 0
                    for (task in it) {
                        if (task.taskStatus != TaskStatus.Active) {
                            taskDone++
                        }
                    }

                    binding.taskDone.text = "$taskDone/${it.size}"
                }
            }
        }
    }

}