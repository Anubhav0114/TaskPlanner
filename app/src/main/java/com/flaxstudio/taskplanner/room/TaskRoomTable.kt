package com.flaxstudio.taskplanner.room

import androidx.annotation.WorkerThread
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverters
import com.flaxstudio.taskplanner.utils.TaskStatus
import com.flaxstudio.taskplanner.utils.TaskStatusConverter
import kotlinx.coroutines.flow.Flow

/*
* id, ProjectName, taskName, description, startTime, endTime, isRemind, tags, taskStatus*/

@Entity(tableName = "project_task")
@TypeConverters(TaskStatusConverter::class)
data class ProjectTask(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "task_id") var taskId: Long,
    @ColumnInfo(name = "project_id") var projectId: Long,
    @ColumnInfo(name = "task_name") var taskName: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "is_remind") var isRemind: Boolean,
    @ColumnInfo(name = "start_time") var startTime: Long,
    @ColumnInfo(name = "end_time") var endTime: Long,
    @ColumnInfo(name = "tags") var tags: String,
    @ColumnInfo(name = "task_status") var taskStatus: TaskStatus,
    )

// Note: task status may be - active, done, not_done

@Dao
interface ProjectTaskDao {

    @Query("SELECT * FROM project_task")
    fun getAllTaskSync(): List<ProjectTask>

    @Insert
    fun addAllTaskSync(tasks: List<ProjectTask>)

    @Query("DELETE FROM project_task")
    fun clearTable()

    @Query("SELECT * FROM project_task WHERE project_id = :projectId ORDER BY start_time ASC")
    fun getAllTaskFromProject(projectId: Long): Flow<List<ProjectTask>>

    @Query("SELECT * FROM project_task WHERE task_id = :taskId LIMIT 1")
    fun getTaskById(taskId: Long): ProjectTask

    @Query("SELECT ROUND(COUNT(CASE WHEN task_status = 1 THEN 1 END) * 100.0 / COUNT(*)) FROM project_task WHERE project_id = :projectId")
    fun getDonePercentage(projectId: Long): Int

    @Query("UPDATE project_task SET task_status = 2 WHERE task_status = 0 AND end_time < :millisecond")
    fun checkAndUpdateFailedTask(millisecond: Long)

    @Query("SELECT * FROM project_task WHERE start_time <= :todayTime AND end_time >= :todayTime")
    fun getAllTodayTask(todayTime: Long): Flow<List<ProjectTask>>

    @Query("UPDATE project_task SET task_name = :taskName, description = :description, is_remind = :isRemind, start_time = :startTime, end_time = :endTime, tags = :tags, task_status = :taskStatus WHERE task_id = :taskId")
    fun updateTask(taskId: Long, taskName: String, description: String, isRemind: Boolean, startTime: Long, endTime: Long, tags: String, taskStatus: Int)

    @Insert
    fun addTask(task: ProjectTask)

    @Query("DELETE FROM project_task WHERE task_id = :taskId")
    fun deleteTask(taskId: Long)
}

class ProjectTaskRepository(private val projectTaskDao: ProjectTaskDao) {

    @WorkerThread
    suspend fun insert(task: ProjectTask) {
        projectTaskDao.addTask(task)
    }

    @WorkerThread
    suspend fun getTaskDonePercentage(projectId: Long): Int {
        return projectTaskDao.getDonePercentage(projectId)
    }

    @WorkerThread
    suspend fun getAllTaskFromProject(projectId: Long): Flow<List<ProjectTask>>{
        return projectTaskDao.getAllTaskFromProject(projectId)
    }

    @WorkerThread
    suspend fun update(task: ProjectTask) {
        projectTaskDao.updateTask(task.taskId, task.taskName, task.description, task.isRemind, task.startTime, task.endTime, task.tags, task.taskStatus.ordinal)
    }


    @WorkerThread
    suspend fun delete(taskId: Long) {
        projectTaskDao.deleteTask(taskId)
    }

    @WorkerThread
    suspend fun getTaskById(taskId: Long): ProjectTask {
        return projectTaskDao.getTaskById(taskId)
    }

    @WorkerThread
    suspend fun checkAndUpdateTaskStatus(currentMillisecond: Long) {
        projectTaskDao.checkAndUpdateFailedTask(currentMillisecond)
    }

    @WorkerThread
    suspend fun getAllTodayTask(todayTime: Long): Flow<List<ProjectTask>>{
        return projectTaskDao.getAllTodayTask(todayTime)
    }

    @WorkerThread
    suspend fun getAllTaskSync(): List<ProjectTask>{
        return projectTaskDao.getAllTaskSync()
    }

    @WorkerThread
    suspend fun saveAllTaskSync(tasks: List<ProjectTask>) {
        projectTaskDao.clearTable()
        projectTaskDao.addAllTaskSync(tasks)
    }

}


