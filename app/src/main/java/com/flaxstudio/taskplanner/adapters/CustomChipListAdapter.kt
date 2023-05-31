package com.flaxstudio.taskplanner.adapters

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.flaxstudio.taskplanner.R
import com.flaxstudio.taskplanner.customview.CustomChip
import com.flaxstudio.taskplanner.utils.ChipData
import kotlin.math.min

class CustomChipListAdapter(private val chipsParentView: LinearLayout, private val contextApp: Context, private val itemClickListener: OnItemClickListener) {
    private val previousList = ArrayList<ChipData>()
    private val inflater: LayoutInflater = LayoutInflater.from(contextApp)


    interface OnItemClickListener {
        fun onItemClick(chipData: ChipData, itemIndex: Int)
        fun onIconClick()
    }

    init {
        chipsParentView.removeAllViews()
        val item = inflater.inflate(R.layout.chip_item, chipsParentView, false) as CustomChip
        item.setIconView(true)
        item.setOnClickListener {
            itemClickListener.onIconClick()
        }
        chipsParentView.addView(item)
    }


    fun submitList(list: List<ChipData>){

        // calculating matchable max index
        val maxIndex = min(previousList.size, list.size)
        var index = 0
        while(index < maxIndex){
            if(!isEqual(previousList[index], list[index])){
                updateView(index, list[index])
                previousList[index] = list[index]
            }
            index++
        }

        // adding
        if(list.size > previousList.size){
            while (index <= list.lastIndex){
                previousList.add(list[index])
                addView(list[index])
                index++
            }

        // removing
        }else if(list.size < previousList.size){

            val pendingRemove = ArrayList<ChipData>()
            val removeIndex = index
            while (index <= previousList.lastIndex){
                pendingRemove.add(previousList[index])
                removeView(removeIndex)
                index++
            }

            for(item in pendingRemove){
                previousList.remove(item)
            }
        }

    }

    private fun addView(chipData: ChipData){
        val item = inflater.inflate(R.layout.chip_item, chipsParentView, false) as CustomChip
        item.setText(chipData.name)
        item.setInfoText(chipData.chipCount.toString())
        item.setActive(chipData.isActive)
        item.setOnClickListener {
            val index = chipsParentView.indexOfChild(it)
            itemClickListener.onItemClick(previousList[index], index)
        }
        chipsParentView.addView(item, chipsParentView.childCount - 1)
    }

    private fun removeView(index: Int){
        chipsParentView.removeViewAt(index)
    }

    private fun updateView(index: Int, chipData: ChipData){
        val chip = chipsParentView.getChildAt(index) as CustomChip
        chip.setText(chipData.name)
        chip.setInfoText(chipData.chipCount.toString())
        chip.setActive(chipData.isActive)
    }

    private fun isEqual(prev:ChipData, curr: ChipData): Boolean {
        if(prev.id != curr.id) return false
        if(prev.name != curr.name) return false
        if(prev.chipCount != curr.chipCount) return false
        if(prev.isActive != curr.isActive) return false
        return true
    }
}