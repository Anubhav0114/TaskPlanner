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
    @ColumnInfo(name = "project_name") var projectName: String,
    @ColumnInfo(name = "is_notify") var isNotify: Boolean,
    @ColumnInfo(name = "is_pinned") var isPinned: Boolean,
    )

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectById(projectId: Int): Project

    @Query("SELECT * FROM projects")
    fun getAllProjects(): List<Project>

    @Query("SELECT * FROM projects WHERE is_pinned = 1")
    fun getAllPinnedProjects(): List<Project>

    @Update
    fun updateProject(project: Project)

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
       projectDao.updateProject(project)
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
    suspend fun getProjectById(projectId: Int): Project {
        return projectDao.getProjectById(projectId)
    }

}


