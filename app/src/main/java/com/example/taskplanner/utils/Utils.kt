package com.example.taskplanner.utils

import android.content.res.Resources
import android.util.TypedValue

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics).toInt()

val Number.spToPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this.toFloat(),
    Resources.getSystem().displayMetrics).toInt()



fun generateUniqueId(): String{
    return System.currentTimeMillis().toString()
}