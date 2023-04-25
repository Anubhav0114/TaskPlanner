package com.example.taskplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskplanner.room.Project
import com.example.taskplanner.room.ProjectRepository
import com.example.taskplanner.room.ProjectTaskRepository
import com.example.taskplanner.utils.generateUniqueId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(private val projectRepository: ProjectRepository, private val taskRepository: ProjectTaskRepository): ViewModel() {



    // ------------------------- Room Project Handler Code ------------------------------------
    fun getAllProjectsTask(callback: (List<Project>) -> Unit) = viewModelScope.launch(Dispatchers.Default){
        val projects = projectRepository.getAllProjects()
        withContext(Dispatchers.Main){
            callback(projects)
        }
    }

    fun createNewProject(projectName: String, callback: () -> Unit) = viewModelScope.launch(Dispatchers.Default) {
        val newProject = Project(0, generateUniqueId(), projectName, "All",
            isNotify = true,
            isPinned = false
        )
        projectRepository.insert(newProject)
        withContext(Dispatchers.Main){
            callback()
        }
    }



    // ------------------------- Room Task Handler Code ------------------------------------


}


// this will allow us to pass parameter to view model
class MainActivityViewModelFactory(private val projectRepository: ProjectRepository, private val taskRepository: ProjectTaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(projectRepository, taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}