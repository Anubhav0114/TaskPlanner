package com.example.taskplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskplanner.room.Project
import com.example.taskplanner.room.ProjectRepository
import com.example.taskplanner.room.ProjectTask
import com.example.taskplanner.room.ProjectTaskRepository
import com.example.taskplanner.utils.generateUniqueId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(private val projectRepository: ProjectRepository, private val taskRepository: ProjectTaskRepository): ViewModel() {



    // ------------------------- Room Project Handler Code ------------------------------------
    fun getAllProjects(callback: (List<Project>) -> Unit) = viewModelScope.launch(Dispatchers.Default){
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

    fun getProjectById(projectId: Long, callback: (Project) -> Unit) = viewModelScope.launch(Dispatchers.Default) {
        val project = projectRepository.getProjectById(projectId)
        withContext(Dispatchers.Main){
            callback(project)
        }
    }



    // ------------------------- Room Task Handler Code ------------------------------------
    fun getProjectTaskById(projectId: Long, taskId: Long , callback: (ProjectTask) -> Unit) = viewModelScope.launch(Dispatchers.Default){
        val projects = taskRepository.getTaskById(projectId, taskId)
        withContext(Dispatchers.Main){
            callback(projects)
        }
    }

    fun createProjectTask(projectTask: ProjectTask , callback: () -> Unit) = viewModelScope.launch(Dispatchers.Default){
        taskRepository.insert(projectTask)
        withContext(Dispatchers.Main){
            callback()
        }
    }

    fun updateProjectTask(projectTask: ProjectTask , callback: () -> Unit) = viewModelScope.launch(Dispatchers.Default){
        taskRepository.update(projectTask)
        withContext(Dispatchers.Main){
            callback()
        }
    }

//    fun getAllTaskFromProject(projectId: Long , callback: (List<ProjectTask>) -> Unit) = viewModelScope.launch(Dispatchers.Default){
//
//        val tasks = taskRepository.getAllTaskFromProject(projectId)
//        withContext(Dispatchers.Main){
//            callback(tasks)
//        }
//    }

    suspend fun getAllTaskFromProject(projectId: Long): Flow<List<ProjectTask>> {
        return taskRepository.getAllTaskFromProject(projectId)
    }





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