package com.flaxstudio.taskplanner

import android.app.Application
import com.flaxstudio.taskplanner.room.AppDatabase
import com.flaxstudio.taskplanner.room.ProjectRepository
import com.flaxstudio.taskplanner.room.ProjectTaskRepository


class ProjectApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { AppDatabase.getDatabase(this) }
    val projectRepository by lazy { ProjectRepository(database.projectDao()) }
    val taskRepository by lazy { ProjectTaskRepository(database.taskDao()) }

    override fun onCreate() {
        super.onCreate()
    }
}