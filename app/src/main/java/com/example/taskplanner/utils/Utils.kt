package com.example.taskplanner.utils

import android.content.res.Resources
import android.util.TypedValue

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics).toInt()

class Vector2{
    var x = 0f
    var y = 0f

    fun setValue(x: Float, y: Float){
        this.x = x
        this.y = y
    }
}