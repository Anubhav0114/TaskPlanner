package com.example.taskplanner.room

import androidx.annotation.WorkerThread
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.example.taskplanner.utils.TaskStatus
import com.example.taskplanner.utils.TaskStatusConverter
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

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

    @Query("SELECT * FROM project_task WHERE project_id = :projectId ORDER BY start_time ASC")
    fun getAllTaskFromProject(projectId: Long): Flow<List<ProjectTask>>


    @Query("SELECT * FROM project_task WHERE project_id = :projectId AND task_id = :taskId LIMIT 1")
    fun getTaskById(projectId: Long, taskId: Long): ProjectTask

    @Query("SELECT ROUND(COUNT(CASE WHEN task_status = 1 THEN 1 END) * 100.0 / COUNT(*)) FROM project_task WHERE project_id = :projectId")
    fun getDonePercentage(projectId: Long): Int

    @Update
    fun updateTask(task: ProjectTask)

    @Insert
    fun addTask(task: ProjectTask)

    @Delete
    fun deleteTask(task: ProjectTask)
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
        projectTaskDao.updateTask(task)
    }


    @WorkerThread
    suspend fun delete(task: ProjectTask) {
        projectTaskDao.deleteTask(task)
    }

    @WorkerThread
    suspend fun getTaskById(projectId: Long, taskId: Long): ProjectTask {
        return projectTaskDao.getTaskById(projectId, taskId)
    }

}


