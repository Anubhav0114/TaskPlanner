package com.flaxstudio.taskplanner.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.flaxstudio.taskplanner.utils.toPx


class ScalePageTransformer : ViewPager2.PageTransformer {
    private val gap = 16.toPx
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        val pageHeight = page.height

        if (position < -1) { // Page is off-screen to the left
            page.translationX = 0F;
        } else if (position <= 1) { // Page is currently visible
            page.translationX = -position * gap;
        } else { // Page is off-screen to the right
            page.translationX = 0F;
        }
    }


}