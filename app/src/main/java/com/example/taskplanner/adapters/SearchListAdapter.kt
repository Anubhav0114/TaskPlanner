package com.example.taskplanner.adapters

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskplanner.R

import com.example.taskplanner.room.Project

class SearchListAdapter(private val itemClickListener: OnItemClickListener): ListAdapter<Project, SearchListAdapter.CustomViewHolder>(ItemDiffCallback()){
    var searchQuery = ""

    fun getSpannableText(query: String, text: String): SpannableString {
        val spannableString = SpannableString(text)

        val regex = Regex("(?i)$query")
        val matches = regex.findAll(text)
        val indices = matches.map { it.range.first }

        indices.forEach { index ->
            spannableString.setSpan(StyleSpan(Typeface.BOLD), index, index + query.length, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
        }

        return spannableString
    }

    interface OnItemClickListener {
        fun onItemClick(project: Project, view: View)
    }

    inner class CustomViewHolder(itemView: ConstraintLayout, private val itemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(project: Project){

            itemView.findViewById<TextView>(R.id.projectSearchName).text = getSpannableText(searchQuery, project.projectName)

            itemView.transitionName = project.projectId.toString()
            itemView.setOnClickListener {
                itemClickListener.onItemClick(project, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false) as ConstraintLayout

        return CustomViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean = false
    }
}