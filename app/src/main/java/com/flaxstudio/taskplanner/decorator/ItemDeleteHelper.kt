package com.flaxstudio.taskplanner.decorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class ItemDeleteHelper(private val icon: Drawable, backgroundColor: Int) {

    val backgroundPaint = Paint().apply {
        color = backgroundColor
        style = Paint.Style.FILL
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.RIGHT
    ) {
        private val swipeThreshold = 0.5f // Customize the swipe threshold here

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // No implementation needed here for Gmail-like swipe behavior
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
            var xPos = dX
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.height.toFloat()
                val itemWidth = itemView.width.toFloat()

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

                    if (xPos >= itemHeight) {
                        xPos = itemHeight
                        viewHolder.itemView.isEnabled = false

                    } else {
                        // Item is swiped within the threshold, restore item to its original position
                        viewHolder.itemView.isEnabled = true
                    }

                    val bacTop = itemView.top.toFloat()
                    c.drawRect(itemView.marginLeft.toFloat(), bacTop, xPos + itemView.marginLeft * 2, bacTop + itemHeight, backgroundPaint)
                    DrawableCompat.setTint(icon, Color.WHITE)
                    icon.draw(c)
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, xPos, dY, actionState, isCurrentlyActive)
        }




    }

    fun setup(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}