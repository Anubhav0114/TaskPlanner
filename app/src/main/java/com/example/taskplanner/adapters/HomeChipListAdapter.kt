package com.example.taskplanner.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskplanner.R
import com.example.taskplanner.customview.CustomChip
import com.example.taskplanner.utils.ChipData

class HomeChipListAdapter(): ListAdapter<ChipData, HomeChipListAdapter.CustomViewHolder>(ItemDiffCallback()){

    inner class CustomViewHolder(itemView: RelativeLayout) : RecyclerView.ViewHolder(itemView) {
        fun bind(chipData: ChipData, isLast: Boolean){

            itemView.findViewById<CustomChip>(R.id.customChip).apply {
                setIconView(isLast)
                if(!isLast){
                    setText(chipData.name)
                    setInfoText(chipData.chipCount.toString())
                    setActive(chipData.isActive)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chip_item, parent, false) as RelativeLayout


        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position), (itemCount - 1) == position)
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<ChipData>() {
    override fun areItemsTheSame(oldItem: ChipData, newItem: ChipData): Boolean = oldItem == newItem
    override fun areContentsTheSame(oldItem: ChipData, newItem: ChipData): Boolean = oldItem == newItem
}