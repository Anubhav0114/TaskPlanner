package com.example.taskplanner.room

import androidx.annotation.WorkerThread
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

/*
* id, ProjectName, taskName, description, startTime, endTime, isRemind, tags, taskStatus*/
@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "project_id") var projectId: String,
    @ColumnInfo(name = "project_name") var projectName: String,
    @ColumnInfo(name = "collection_name") var collectionName: String,
    @ColumnInfo(name = "is_notify") var isNotify: Boolean,
    @ColumnInfo(name = "is_pinned") var isPinned: Boolean,
    )

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects WHERE project_id = :projectId LIMIT 1")
    fun getProjectById(projectId: String): Project

    @Query("SELECT * FROM projects")
    fun getAllProjects(): List<Project>

    @Query("SELECT * FROM projects WHERE is_pinned = 1")
    fun getAllPinnedProjects(): List<Project>

    @Query("UPDATE projects SET project_name = :projectName, collection_name = :collectionName, is_notify = :isNotify, is_pinned = :isPinned WHERE project_id = :projectId")
    fun updateProject(projectId: String, projectName: String, collectionName: String, isNotify: Boolean, isPinned: Boolean)

    @Insert
    fun addProject(project: Project)

    @Delete
    fun deleteProject(project: Project)
}

class ProjectRepository(private val projectDao: ProjectDao) {

    @WorkerThread
    suspend fun insert(project: Project) {
        projectDao.addProject(project)
    }

    @WorkerThread
    suspend fun getAllProjects(): List<Project> {
        return projectDao.getAllProjects()
    }

    @WorkerThread
    suspend fun update(project: Project) {
       projectDao.updateProject(project.projectId, project.projectName, project.collectionName, project.isNotify, project.isPinned)
    }

    @WorkerThread
    suspend fun getPinnedProject(): List<Project> {
       return projectDao.getAllPinnedProjects()
    }


    @WorkerThread
    suspend fun delete(project: Project) {
        projectDao.deleteProject(project)
    }

    @WorkerThread
    suspend fun getProjectById(projectId: String): Project {
        return projectDao.getProjectById(projectId)
    }

}


