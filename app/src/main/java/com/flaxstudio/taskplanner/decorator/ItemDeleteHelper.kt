package com.flaxstudio.taskplanner.decorator

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.flaxstudio.taskplanner.adapters.ProjectTaskListAdapter
import com.flaxstudio.taskplanner.room.ProjectTask
import com.flaxstudio.taskplanner.utils.toPx
import com.google.android.material.snackbar.Snackbar
import kotlin.math.abs

class ItemDeleteHelper(private val icon: Drawable, backgroundColor: Int, private val itemDeleteListener: ItemDeleteHelper.OnItemDeleteListener) {

    interface OnItemDeleteListener{
        fun onItemDelete(index: Int)
    }

    val backgroundPaint = Paint().apply {
        color = backgroundColor
        style = Paint.Style.FILL
    }

    val round = 8.toPx.toFloat()

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.RIGHT
    ) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition
            itemDeleteListener.onItemDelete(pos)

        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            // Swipe-to-delete animation
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.height.toFloat()
                val itemWidth = itemView.width.toFloat()

                Log.e("------------", isCurrentlyActive.toString())

                if (dX > 0) { // Swiping right
                    // Calculate the position for the delete icon
                    val iconMargin = (itemHeight - icon.intrinsicHeight) / 2
                    val iconTop = itemView.top + (itemHeight - icon.intrinsicHeight) / 2
                    val iconBottom = iconTop + icon.intrinsicHeight
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + iconMargin + icon.intrinsicWidth

                    // Draw the delete icon on the canvas
                    icon.setBounds(
                        iconLeft.toInt(),
                        iconTop.toInt(),
                        iconRight.toInt(),
                        iconBottom.toInt()
                    )


                    val bacTop = itemView.top.toFloat()
                    c.drawRoundRect(
                        itemView.marginLeft.toFloat(),
                        bacTop,
                        itemWidth + itemView.marginRight,
                        bacTop + itemHeight,
                        round,
                        round,
                        backgroundPaint
                    )

                    DrawableCompat.setTint(icon, Color.WHITE)
                    icon.draw(c)
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }




    }

    fun setup(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}