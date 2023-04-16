package com.example.taskplanner.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.example.taskplanner.R

class TripleOptionCheckBox(context: Context, attrs: AttributeSet?): AppCompatCheckBox(context, attrs) {

    private var buttonState: CheckBoxState = CheckBoxState.Default

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        return super.onCreateDrawableState(extraSpace)

//        val state = super.onCreateDrawableState(extraSpace + 2)
//
//        if(buttonState == CheckBoxState.Checked){
//            //mergeDrawableStates(state, intArrayOf(R.attr.))
//        }
//
//        return state
    }


    enum class CheckBoxState{
        Checked,
        UnChecked,
        Default
    }
}