package com.example.taskplanner.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskplanner.R
import com.example.taskplanner.room.Project


class HomeProjectListAdapter(): ListAdapter<Project, HomeProjectListAdapter.CustomViewHolder>(ItemDiffCallback()){

    inner class CustomViewHolder(itemView: ConstraintLayout) : RecyclerView.ViewHolder(itemView) {
        fun bind(project: Project){
            itemView.findViewById<TextView>(R.id.project_title).text = project.projectName
            itemView.findViewById<TextView>(R.id.collection_name).text = project.collectionName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_project_item, parent, false) as ConstraintLayout

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean = oldItem == newItem
    }
}

