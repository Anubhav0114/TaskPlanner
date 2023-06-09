package com.flaxstudio.taskplanner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flaxstudio.taskplanner.R
import com.flaxstudio.taskplanner.room.ProjectTask
import com.flaxstudio.taskplanner.utils.DateTimeManager
import com.flaxstudio.taskplanner.utils.TaskStatus

class ProjectTaskListAdapter(private val itemClickListener: OnItemClickListener): ListAdapter<ProjectTask, ProjectTaskListAdapter.CustomViewHolder>(ItemDiffCallback()){

    private val dateManager = DateTimeManager()
    interface OnItemClickListener {
        fun onItemClick(projectTask: ProjectTask, view: View)
        fun onCheckChangeListener(projectTask: ProjectTask)
    }

    inner class CustomViewHolder(itemView: ConstraintLayout, private val itemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(projectTask: ProjectTask){
            itemView.findViewById<TextView>(R.id.title).text = projectTask.taskName
            itemView.findViewById<TextView>(R.id.date_time).text = dateManager.unixMillToDateString(projectTask.startTime)

            itemView.transitionName = projectTask.taskId.toString()
            itemView.setOnClickListener {
                itemClickListener.onItemClick(projectTask, it)
            }

            val checkBox = itemView.findViewById<CheckBox>(R.id.statusCheckBox)
            when (projectTask.taskStatus) {
                TaskStatus.Active -> {
                    checkBox.isChecked = false
                    checkBox.isEnabled = true
                }
                TaskStatus.Done -> {
                    checkBox.isChecked = true
                    checkBox.isEnabled = true
                }
                else -> {
                    checkBox.isChecked = false
                    checkBox.isEnabled = false
                    return
                }
            }

            checkBox.setOnClickListener {
                it as CheckBox
                if(it.isChecked){
                    projectTask.taskStatus = TaskStatus.Done
                }else{
                    projectTask.taskStatus = TaskStatus.Active
                }
                itemClickListener.onCheckChangeListener(projectTask)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false) as ConstraintLayout

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