package com.example.taskplanner.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskplanner.R
import com.example.taskplanner.customview.CircularProgress
import com.example.taskplanner.room.Project
import kotlin.math.roundToInt


class HomeProjectListAdapter(private val itemClickListener: OnItemClickListener): ListAdapter<Project, HomeProjectListAdapter.CustomViewHolder>(ItemDiffCallback()){

    interface OnItemClickListener {
        fun onItemClick(project: Project)
    }

    inner class CustomViewHolder(itemView: ConstraintLayout, private val itemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(project: Project){
            itemView.findViewById<TextView>(R.id.project_title).text = project.projectName
            itemView.findViewById<TextView>(R.id.collection_name).text = project.collectionName

            itemView.findViewById<TextView>(R.id.progress_value).text = "${project.donePercent}%"
            itemView.findViewById<CircularProgress>(R.id.circularProgress).setMaxProgressValue(1000)
            itemView.findViewById<CircularProgress>(R.id.circularProgress).setProgressValue((project.donePercent / 100f * 1000).roundToInt())

            itemView.setOnClickListener {
                itemClickListener.onItemClick(project)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_project_item, parent, false) as ConstraintLayout

        return CustomViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean = oldItem == newItem
    }
}

