package com.flaxstudio.taskplanner.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.taskplanner.utils.toPx


class HomeTaskDecorator: RecyclerView.ItemDecoration() {

    private val margin = 16.toPx
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (position == itemCount - 1) {
            outRect.right = margin
        } else {
            outRect.right = 0
        }
    }

}