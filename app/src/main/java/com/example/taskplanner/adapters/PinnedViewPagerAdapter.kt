package com.example.taskplanner.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.taskplanner.fragments.PinnedProjectFragment
import com.example.taskplanner.room.Project

class PinnedViewPagerAdapter(private val fragment: Fragment): FragmentStateAdapter(fragment) {
    private val projects = ArrayList<Project>()
    private val dailyProject = Project(0,0L, "Today Task", "", isNotify = false, isPinned = false)

    fun addProjects(list: List<Project>){
        projects.clear()
        projects.add(dailyProject)
        projects.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    override fun createFragment(position: Int): Fragment {
        return if(position == 0){
            PinnedProjectFragment(true, projects[position])
        }else{
            PinnedProjectFragment(false, projects[position])
        }
    }
}