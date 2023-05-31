package com.flaxstudio.taskplanner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskplanner.R
import com.example.taskplanner.room.ProjectTask
import com.example.taskplanner.utils.DateTimeManager

class HomeTodayTaskListAdapter(private val itemClickListener: OnItemClickListener): ListAdapter<ProjectTask, HomeTodayTaskListAdapter.CustomViewHolder>(ItemDiffCallback()){

    val dateTimeManager = DateTimeManager()

    interface OnItemClickListener {
        fun onItemClick(task: ProjectTask, view: View)
    }

    inner class CustomViewHolder(itemView: ConstraintLayout, private val itemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: ProjectTask){
            itemView.findViewById<TextView>(R.id.task_title).text = task.taskName
            itemView.findViewById<TextView>(R.id.task_description).text = task.description
            itemView.findViewById<TextView>(R.id.task_time).text = "Till ${dateTimeManager.unixMillToDateString(task.endTime)}"

            itemView.transitionName = task.taskId.toString()
            itemView.setOnClickListener {
                itemClickListener.onItemClick(task, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_task_item, parent, false) as ConstraintLayout

        return CustomViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<ProjectTask>() {
        override fun areItemsTheSame(oldItem: ProjectTask, newItem: ProjectTask): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: ProjectTask, newItem: ProjectTask): Boolean = oldItem == newItem
    }
}