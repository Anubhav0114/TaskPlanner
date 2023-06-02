package com.flaxstudio.taskplanner.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flaxstudio.taskplanner.fragments.PinnedProjectFragment
import com.flaxstudio.taskplanner.room.Project

class PinnedViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    private val projects = ArrayList<Project>()
    private val dailyProject = Project(0,100L, "Today Task", 0L, 0,isNotify = false, isPinned = false, isDeleted = false)

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