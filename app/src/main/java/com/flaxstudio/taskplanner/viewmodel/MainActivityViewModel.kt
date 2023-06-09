package com.flaxstudio.taskplanner.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flaxstudio.taskplanner.room.Project
import com.flaxstudio.taskplanner.room.ProjectRepository
import com.flaxstudio.taskplanner.room.ProjectTask
import com.flaxstudio.taskplanner.room.ProjectTaskRepository
import com.flaxstudio.taskplanner.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivityViewModel(private val projectRepository: ProjectRepository, private val taskRepository: ProjectTaskRepository): ViewModel() {

    // ----------------------- Animation state -------------------
    // home fragment
    var motionProgress = 0f
    var projectRecyclerViewPosition = 0
    var viewpagerIndex = 0
    var homeTaskPosition = 0

    // project fragment
    var selectedChipIndex = 1





    private val dateTimeManager = DateTimeManager()
    private val gson = Gson()
    init {
        checkTaskStatus()
    }


    // sp manager
    lateinit var spManager: SharedPreferenceManager
    fun setupViewModel(contextApp: Context){
        spManager = SharedPreferenceManager(viewModelScope, contextApp)
    }



    // ------------------------- Room Project Handler Code ------------------------------------
    suspend fun getAllProjects(): Flow<List<Project>>{
        return projectRepository.getAllProjects()
    }

    suspend fun getAllPinnedProject(): Flow<List<Project>>{
        return projectRepository.getPinnedProject()
    }

    fun createNewProject(projectName: String, collectionId: Long, callback: () -> Unit) = viewModelScope.launch(Dispatchers.Default) {
        val newProject = Project(0, generateUniqueId(), projectName, collectionId,0,
            isNotify = true,
            isPinned = false,
            isDeleted = false
        )
        projectRepository.insert(newProject)
        withContext(Dispatchers.Main){
            callback()
        }
    }

    fun toggleProjectTempDelete(projectId: Long, isDeleted: Boolean) = viewModelScope.launch(Dispatchers.Default){
        projectRepository.toggleTempProjectDelete(projectId, isDeleted)
    }



    fun getSyncData(callback: (String) -> Unit) = viewModelScope.launch(Dispatchers.Default) {
        val allTask = taskRepository.getAllTaskSync()
        val allProject = projectRepository.getAllProjectSync()
        val data = SyncData(spManager.getSyncData(), allProject, allTask)
        val json = gson.toJson(data)
        withContext(Dispatchers.Main){
            callback(json)
        }
    }

    //TODO: call getSync data with callback call saveSyncData
    fun saveSyncData(data: String, callback: (Boolean) -> Unit) = viewModelScope.launch(Dispatchers.Default){
        try {
            Log.d("TAG", "saveSyncData: $data")
            val syncData = gson.fromJson(data, SyncData::class.java)
            spManager.saveSyncData(syncData.collections)
            projectRepository.saveAllProjectSync(syncData.allProjects)
            taskRepository.saveAllTaskSync(syncData.allTasks)
            withContext(Dispatchers.Main){
                callback(true)
            }
        }catch (ex: Exception){
            ex.printStackTrace()
            withContext(Dispatchers.Main){
                callback(false)
            }
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

    fun deleteCollectionAllProject(collectionId: Long) = viewModelScope.launch(Dispatchers.Default) {
        projectRepository.deleteCollectionAllProject(collectionId)
        withContext(Dispatchers.Main){
            spManager.removeCollectionItem(collectionId)
        }
    }



    // ------------------------- Room Task Handler Code ------------------------------------
    fun getProjectTaskById(taskId: Long , callback: (ProjectTask) -> Unit) = viewModelScope.launch(Dispatchers.Default){
        val projects = taskRepository.getTaskById(taskId)
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

    fun deleteTask(taskId: Long, callback: () -> Unit) = viewModelScope.launch(Dispatchers.Default) {
        taskRepository.delete(taskId)
        withContext(Dispatchers.Main){
            callback()
        }
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