package com.example.taskplanner.fragments


import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskplanner.MainActivity
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.Task
import com.example.taskplanner.adapters.HomeChipListAdapter
import com.example.taskplanner.adapters.HomeProjectListAdapter
import com.example.taskplanner.databinding.FragmentHomeBinding
import com.example.taskplanner.room.Project
import com.example.taskplanner.utils.ChipData
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(
            (requireActivity().application as ProjectApplication).projectRepository,
            (requireActivity().application as ProjectApplication).taskRepository
        )
    }
    private lateinit var contextApp: Context


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contextApp = requireContext()

        setupData()

        binding.navButton.setOnClickListener{
            (activity as MainActivity).openNavDrawer()
        }

        binding.newProjectBtn.setOnClickListener {
            // Alert Box
            dialog()
        }
    }

    private fun dialog(){
        val dialogView = LayoutInflater.from(contextApp).inflate(R.layout.add_project_dialog, null)
        val builder = AlertDialog.Builder(contextApp)
        builder.setView(dialogView)

        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.dialog_create)?.setOnClickListener {

            val name = dialogView.findViewById<EditText>(R.id.dialog_input).text.toString().trim().replace("\\s+", " ")
            if(name.isBlank()){
                Toast.makeText(contextApp, "Name must not be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mainActivityViewModel.createNewProject(name){
                dialog.dismiss()
                Toast.makeText(contextApp, "Project created", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<Button>(R.id.dialog_cancel)?.setOnClickListener {
            // handle Cancel button click
            Toast.makeText(contextApp,"Close It",Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialog.show()

    }


    private fun setupData(){
        setupRecentRecyclerView()
    }


    private lateinit var chipListAdapter: HomeChipListAdapter
    private lateinit var projectListAdapter: HomeProjectListAdapter
    private fun setupRecentRecyclerView(){
        // 1
        // setup data in chips
        chipListAdapter = HomeChipListAdapter()
        binding.homeChipRecyclerview.adapter = chipListAdapter
        val chipLayoutManager = LinearLayoutManager(contextApp, LinearLayoutManager.HORIZONTAL, false)
        binding.homeChipRecyclerview.layoutManager = chipLayoutManager

        val chipList = arrayListOf<ChipData>()
        chipList.add(ChipData("All", 5, true))
        chipList.add(ChipData("Android", 10, false))
        chipList.add(ChipData("Game", 17, false))
        chipList.add(ChipData("", 0, false))
        chipListAdapter.submitList(chipList)


        // 2
        // setup data in project adapter
        projectListAdapter = HomeProjectListAdapter(object: HomeProjectListAdapter.OnItemClickListener{
            override fun onItemClick(project: Project) {

                val bundle = Bundle().apply {
                    putLong("project_id", project.projectId)
                }


                findNavController().navigate(R.id.action_homeFragment_to_projectFragment, bundle, null)
            }
        })
        val projectLayoutManager = LinearLayoutManager(contextApp)
        binding.allProjectRecyclerView.adapter = projectListAdapter
        binding.allProjectRecyclerView.layoutManager = projectLayoutManager

        binding.allProjectRecyclerView.addItemDecoration(
            DividerItemDecoration(contextApp, projectLayoutManager.orientation)
        )

        mainActivityViewModel.getAllProjects {
            projectListAdapter.submitList(it)
        }

    }

}

