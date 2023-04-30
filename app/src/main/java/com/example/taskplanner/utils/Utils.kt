package com.example.taskplanner.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.room.TypeConverter
import com.example.taskplanner.room.Project

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics).toInt()

val Number.spToPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this.toFloat(),
    Resources.getSystem().displayMetrics).toInt()



fun generateUniqueId(): Long{
    return System.currentTimeMillis()
}

enum class TaskMode{
    Create,
    Edit,
    View
}

enum class TaskStatus{
    Active,
    Done,
    Failed;

    // create companion for converting enum to no. or vice-versa
    companion object {
        @JvmStatic
        fun fromOrdinal(ordinal: Int) = values()[ordinal]
    }
}


// this class is used for converting enum to num or vice-versa
// It is used by room because room cannot store enum
class TaskStatusConverter {
    @TypeConverter
    fun toInt(status: TaskStatus): Int = status.ordinal

    @TypeConverter
    fun toStatus(ordinal: Int): TaskStatus = TaskStatus.fromOrdinal(ordinal)
}


fun List<Project>.countCollection(collectionName: String): Int {
    var tempCount = 0
    for (project in this){
        if(project.collectionName == collectionName) tempCount++
    }
    return tempCount
}