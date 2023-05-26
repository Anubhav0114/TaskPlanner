package com.example.taskplanner.adapters

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskplanner.R
import com.example.taskplanner.utils.CollectionData
import com.google.android.material.button.MaterialButton


class CollectionListAdapter (private val itemClickListener: OnItemClickListener): ListAdapter<CollectionData, CollectionListAdapter.CustomViewHolder>(ItemDiffCallback()){

    interface OnItemClickListener {
        fun onRenameClick(data: CollectionData)
        fun onDeleteClick(data: CollectionData)
    }

    inner class CustomViewHolder(itemView: ConstraintLayout, private val itemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: CollectionData){
            itemView.findViewById<TextView>(R.id.collectionName).text = data.name
            itemView.findViewById<TextView>(R.id.collectionItem).text = data.count.toString()

            if(data.name == "All"){
                itemView.findViewById<MaterialButton>(R.id.menuButton).visibility = View.INVISIBLE
            }else{
                itemView.findViewById<MaterialButton>(R.id.menuButton).setOnClickListener {
                    showMenu(it, data, itemClickListener)
                }
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


    private fun showMenu(v: View, data: CollectionData, itemClickListener: OnItemClickListener) {
        val popup = PopupMenu(v.context, v,Gravity.END)
        popup.menuInflater.inflate(R.menu.collection_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            return@setOnMenuItemClickListener when (menuItem.itemId) {
                R.id.rename -> {
                    itemClickListener.onRenameClick(data)
                    true
                }
                R.id.delete -> {
                    itemClickListener.onDeleteClick(data)
                    true
                }
                else -> false
            }
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<CollectionData>() {
        override fun areItemsTheSame(oldItem: CollectionData, newItem: CollectionData): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: CollectionData, newItem: CollectionData): Boolean = oldItem == newItem
    }

}