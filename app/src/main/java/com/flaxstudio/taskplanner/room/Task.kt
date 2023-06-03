package com.flaxstudio.taskplanner.room

import android.os.Parcelable
import com.flaxstudio.taskplanner.utils.TaskStatus

data class Task(val id : Int ,val taskId: Long,val projectId: Long,val taskName: String, val description: String, val isRemind: Boolean, var startTime: Long, val endTime :Long , val tags: String,val taskStatus: TaskStatus) : java.io.Serializable
