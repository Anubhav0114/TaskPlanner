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

data class CollectionData(val id: Long, val name: String, val count: Int)
data class CollectionRawData(val id: Long, var name: String){
    override fun toString(): String{
        return "${id},${name}"
    }
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


fun List<Project>.countCollection(collectionId: Long): Int {
    var tempCount = 0
    for (project in this){
        if(project.collectionId == collectionId) tempCount++
    }
    return tempCount
}

fun List<CollectionRawData>.getCollectionName(id: Long): String{
    for (item in this){
        if(item.id == id) return item.name
    }

    return ""
}

fun List<CollectionRawData>.getCollectionId(name: String): Long{
    for (item in this){
        if(item.name == name) return item.id
    }

    return 0
}