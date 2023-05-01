package com.example.taskplanner.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskplanner.MainActivity
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.adapters.CustomChipListAdapter
import com.example.taskplanner.adapters.HomeProjectListAdapter
import com.example.taskplanner.adapters.HomeTodayTaskListAdapter
import com.example.taskplanner.adapters.PinnedViewPagerAdapter
import com.example.taskplanner.databinding.FragmentHomeBinding
import com.example.taskplanner.room.Project
import com.example.taskplanner.room.ProjectTask
import com.example.taskplanner.utils.ChipData
import com.example.taskplanner.utils.DateTimeManager
import com.example.taskplanner.utils.countCollection
import com.example.taskplanner.utils.SharedPreferenceManager
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var allProjects: List<Project> = ArrayList()
    private lateinit var spManager: SharedPreferenceManager
    private lateinit var projectListAdapter: HomeProjectListAdapter
    private lateinit var taskListAdapter: HomeTodayTaskListAdapter
    private lateinit var contextApp: Context
    private lateinit var chipsAdapter: CustomChipListAdapter
    private var collectionNames: List<String> = ArrayList()
    private var selectedChipIndex = 0
    private var selectedCollectionName = "All"
    private lateinit var pinnedProjectAdapter: PinnedViewPagerAdapter

    private val dateTimeManager = DateTimeManager()

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(
            (requireActivity().application as ProjectApplication).projectRepository,
            (requireActivity().application as ProjectApplication).taskRepository
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contextApp = requireContext()
        spManager = SharedPreferenceManager(lifecycleScope, contextApp)

        setupUI()
        addObservers()
        setupListener()
    }

    private fun dialog() {
        val dialogView = LayoutInflater.from(contextApp).inflate(R.layout.add_project_dialog, null)
        val builder = AlertDialog.Builder(contextApp)
        builder.setView(dialogView)

        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.dialog_create)?.setOnClickListener {

            val name = dialogView.findViewById<EditText>(R.id.dialog_input).text.toString().trim()
                .replace("\\s+", " ")
            if (name.isBlank()) {
                Toast.makeText(contextApp, "Name must not be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mainActivityViewModel.createNewProject(name, selectedCollectionName) {
                dialog.dismiss()
                Toast.makeText(contextApp, "Project created", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<Button>(R.id.dialog_cancel)?.setOnClickListener {
            // handle Cancel button click
            Toast.makeText(contextApp, "Close It", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialog.show()

    }


    private fun setupListener() {
        binding.navButton.setOnClickListener {
            (activity as MainActivity).openNavDrawer()
        }

        binding.newProjectBtn.setOnClickListener {
            // Alert Box
            dialog()
        }
    }

    private fun setupUI() {

        // 1
        //  pinned projects
        pinnedProjectAdapter = PinnedViewPagerAdapter(this)
        binding.viewPager2.adapter = pinnedProjectAdapter


        // 2
        // setup data in today task adapter
        taskListAdapter = HomeTodayTaskListAdapter(object : HomeTodayTaskListAdapter.OnItemClickListener{
            override fun onItemClick(task: ProjectTask) {

            }
        })

        val taskLayoutManager = LinearLayoutManager(contextApp, LinearLayoutManager.HORIZONTAL, false)
        binding.todayTaskRecyclerview.adapter = taskListAdapter
        binding.todayTaskRecyclerview.layoutManager = taskLayoutManager


        // 3
        // setup data in project adapter
        projectListAdapter =
            HomeProjectListAdapter(object : HomeProjectListAdapter.OnItemClickListener {
                override fun onItemClick(project: Project) {

                    val bundle = Bundle().apply {
                        putLong("project_id", project.projectId)
                    }

                    findNavController().navigate(
                        R.id.action_homeFragment_to_projectFragment,
                        bundle,
                        null
                    )
                }
            })
        val projectLayoutManager = LinearLayoutManager(contextApp)
        binding.allProjectRecyclerView.adapter = projectListAdapter
        binding.allProjectRecyclerView.layoutManager = projectLayoutManager

//        binding.allProjectRecyclerView.addItemDecoration(
//            DividerItemDecoration(contextApp, projectLayoutManager.orientation)
//        )

        // 4
        chipsAdapter = CustomChipListAdapter(binding.chipsContainer, contextApp, object : CustomChipListAdapter.OnItemClickListener{
            override fun onItemClick(chipData: ChipData, itemIndex: Int) {
                selectedChipIndex = itemIndex
                selectedCollectionName = chipData.name
                updateChips()
                updateRecyclerView()

            }

            override fun onIconClick() {
                // TODO add new collection
                spManager.addCollectionItem("Nitesh")
            }
        })

    }


    private fun addObservers() {

        lifecycleScope.launch {
            mainActivityViewModel.getAllProjects().collect {
                allProjects = it
                updateRecyclerView()
            }
        }

        lifecycleScope.launch{
            mainActivityViewModel.getAllPinnedProject().collect{
                // updating view pager
                pinnedProjectAdapter.addProjects(it)
            }
        }

        lifecycleScope.launch {
            spManager.getCollection().collect {
                collectionNames = it
                updateChips()
            }
        }

        lifecycleScope.launch{
            mainActivityViewModel.getAllTodayTasks(dateTimeManager.getTomorrowDate()).collect{
                taskListAdapter.submitList(it)
            }
        }
    }


    private fun updateChips(){
        val list = ArrayList<ChipData>()
        for(index in collectionNames.indices){
            val isActive = index == selectedChipIndex
            val count = if(collectionNames[index] == "All"){
                allProjects.size
            }else{
                allProjects.countCollection(collectionNames[index])
            }
            list.add(ChipData(index, collectionNames[index], count, isActive))
        }
        chipsAdapter.submitList(list)

    }

    private fun updateRecyclerView(){

        val filteredProject = ArrayList<Project>()
        if(selectedCollectionName != "All"){
            for (project in allProjects){
                if(project.collectionName == selectedCollectionName){
                    filteredProject.add(project)
                }
            }
            projectListAdapter.submitList(filteredProject)
        }else{
            projectListAdapter.submitList(allProjects)
        }

        updateChips()
    }
}

