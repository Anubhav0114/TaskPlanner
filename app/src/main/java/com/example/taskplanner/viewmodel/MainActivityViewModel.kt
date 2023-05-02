package com.example.taskplanner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskplanner.room.Project
import com.example.taskplanner.room.ProjectRepository
import com.example.taskplanner.room.ProjectTask
import com.example.taskplanner.room.ProjectTaskRepository
import com.example.taskplanner.utils.DateTimeManager
import com.example.taskplanner.utils.generateUniqueId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield


class MainActivityViewModel(private val projectRepository: ProjectRepository, private val taskRepository: ProjectTaskRepository): ViewModel() {

    // ----------------------- Animation state -------------------
    // home fragment
    var motionProgress = 0f






    private val dateTimeManager = DateTimeManager()
    init {
        checkTaskStatus()
    }


    // ------------------------- Room Project Handler Code ------------------------------------
    suspend fun getAllProjects(): Flow<List<Project>>{
        return projectRepository.getAllProjects()
    }

    suspend fun getAllPinnedProject(): Flow<List<Project>>{
        return projectRepository.getPinnedProject()
    }

    fun createNewProject(projectName: String, collectionName: String, callback: () -> Unit) = viewModelScope.launch(Dispatchers.Default) {
        val newProject = Project(0, generateUniqueId(), projectName, collectionName,0,
            isNotify = true,
            isPinned = false
        )
        projectRepository.insert(newProject)
        withContext(Dispatchers.Main){
            callback()
        }
    }

    fun updateProject(project: Project) = viewModelScope.launch(Dispatchers.Default) {
        projectRepository.update(project)
    }

    fun getProjectById(projectId: Long, callback: (Project) -> Unit) = viewModelScope.launch(Dispatchers.Default) {
        val project = projectRepository.getProjectById(projectId)
        withContext(Dispatchers.Main){
            callback(project)
        }
    }

    fun updateProjectProgress(projectId: Long) = viewModelScope.launch(Dispatchers.Default) {
        val percent = taskRepository.getTaskDonePercentage(projectId)
        projectRepository.updateProjectProgress(projectId, percent)
    }

    fun searchProject(searchText: String, callback: (List<Project>) -> Unit) = viewModelScope.launch(Dispatchers.Default){
        val searchResult = projectRepository.searchProject(searchText)
        withContext(Dispatchers.Main){
            callback(searchResult)
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

    suspend fun getAllTaskFromProject(projectId: Long): Flow<List<ProjectTask>> {
        return taskRepository.getAllTaskFromProject(projectId)
    }


    // This function will periodically check and update failed task
    private fun checkTaskStatus() = viewModelScope.launch(Dispatchers.Default) {
        while(true){
            taskRepository.checkAndUpdateTaskStatus(dateTimeManager.currentTimeMillisecond())
            delay(4000)
        }
    }

    suspend fun getAllTodayTasks(todayTime: Long): Flow<List<ProjectTask>>{
        return taskRepository.getAllTodayTask(todayTime)
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