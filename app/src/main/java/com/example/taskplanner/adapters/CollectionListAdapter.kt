package com.example.taskplanner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskplanner.R
import com.example.taskplanner.utils.CollectionData
import com.google.android.material.button.MaterialButton


class CollectionListAdapter (private val itemClickListener: OnItemClickListener): ListAdapter<CollectionData, CollectionListAdapter.CustomViewHolder>(ItemDiffCallback()){

    interface OnItemClickListener {
        fun onMenuClick(data: CollectionData, view: View)
    }

    inner class CustomViewHolder(itemView: ConstraintLayout, private val itemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: CollectionData){
            itemView.findViewById<TextView>(R.id.collectionName).text = data.name
            itemView.findViewById<TextView>(R.id.collectionItem).text = data.count.toString()

            itemView.findViewById<MaterialButton>(R.id.menuButton).setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_item, parent, false) as ConstraintLayout

        return CustomViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<CollectionData>() {
        override fun areItemsTheSame(oldItem: CollectionData, newItem: CollectionData): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: CollectionData, newItem: CollectionData): Boolean = oldItem == newItem
    }

}