package com.flaxstudio.taskplanner.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.flaxstudio.taskplanner.MainActivity
import com.flaxstudio.taskplanner.ProjectApplication
import com.flaxstudio.taskplanner.R
import com.flaxstudio.taskplanner.adapters.CustomChipListAdapter
import com.flaxstudio.taskplanner.adapters.HomeProjectListAdapter
import com.flaxstudio.taskplanner.adapters.HomeTodayTaskListAdapter
import com.flaxstudio.taskplanner.adapters.PinnedViewPagerAdapter
import com.flaxstudio.taskplanner.databinding.FragmentHomeBinding
import com.flaxstudio.taskplanner.decorator.HomeTaskDecorator
import com.flaxstudio.taskplanner.decorator.ItemDeleteHelper
import com.flaxstudio.taskplanner.room.Project
import com.flaxstudio.taskplanner.room.ProjectTask
import com.flaxstudio.taskplanner.room.Users
import com.flaxstudio.taskplanner.utils.ChipData
import com.flaxstudio.taskplanner.utils.CollectionRawData
import com.flaxstudio.taskplanner.utils.DateTimeManager
import com.flaxstudio.taskplanner.utils.TaskStatus
import com.flaxstudio.taskplanner.utils.countCollection
import com.flaxstudio.taskplanner.utils.getCollectionId
import com.flaxstudio.taskplanner.utils.removeSurname
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModel
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.material.transition.MaterialElevationScale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var allProjects: List<Project> = ArrayList()
    private lateinit var projectListAdapter: HomeProjectListAdapter
    private lateinit var taskListAdapter: HomeTodayTaskListAdapter
    private lateinit var contextApp: Context
    private lateinit var chipsAdapter: CustomChipListAdapter
    private var collectionNames = emptyList<CollectionRawData>()
    private var selectedChipIndex = 0
    private var selectedCollectionId = 1234567L
    private lateinit var pinnedProjectAdapter: PinnedViewPagerAdapter
    private lateinit var auth : FirebaseAuth

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

    override fun onPause() {
        super.onPause()
        mainActivityViewModel.motionProgress = binding.motionLayout.progress
        mainActivityViewModel.projectRecyclerViewPosition = (binding.allProjectRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        mainActivityViewModel.viewpagerIndex = binding.viewPager2.currentItem
        mainActivityViewModel.homeTaskPosition = (binding.todayTaskRecyclerview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    private fun restoreState(){

        binding.motionLayout.post{
            binding.motionLayout.progress = mainActivityViewModel.motionProgress
        }

        binding.allProjectRecyclerView.post {
            binding.allProjectRecyclerView.scrollToPosition(mainActivityViewModel.projectRecyclerViewPosition)
        }

        binding.viewPager2.post {
            binding.viewPager2.setCurrentItem(mainActivityViewModel.viewpagerIndex, false)
        }

        binding.todayTaskRecyclerview.post {
            binding.todayTaskRecyclerview.scrollToPosition(mainActivityViewModel.homeTaskPosition)
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // code to update the user name
        auth  = Firebase.auth
        updateUserName()

        // handle animations
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }


        contextApp = requireContext()

        setupUI()
        addObservers()
        setupListener()
        restoreState()

    }

    private fun updateUserName() {
        val currentUser = auth.currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(currentUser).get()
            .addOnSuccessListener { userSnapshot ->

                if (userSnapshot == null){
                    Log.i(TAG , "the user object received is null")
                }else{
                    val signedInUsers = userSnapshot.toObject(Users::class.java)
                    val name = "Hello, ${removeSurname(signedInUsers!!.displayName)}"
                    binding.UserName.text = name
                }
            }
            .addOnFailureListener{ exception ->
                Log.i(TAG , "Failure Fetching User" , exception)
            }
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

            mainActivityViewModel.createNewProject(name, selectedCollectionId) {
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

        binding.searchButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
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
            override fun onItemClick(task: ProjectTask, view: View) {
                exitTransition = MaterialElevationScale(false).apply {
                    duration = 400
                }
                reenterTransition = MaterialElevationScale(true).apply {
                    duration = 400
                }

                val extras = FragmentNavigatorExtras(view to "task_fragment")

                val bundle = Bundle().apply {
                    putBoolean("isCreating", false)
                    putLong("projectId", task.projectId)
                    putLong("taskId", task.taskId)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_taskFragment,
                    bundle,
                    null,
                    extras
                )
            }
        })

        val taskLayoutManager = LinearLayoutManager(contextApp, LinearLayoutManager.HORIZONTAL, false)
        binding.todayTaskRecyclerview.adapter = taskListAdapter
        binding.todayTaskRecyclerview.addItemDecoration(HomeTaskDecorator())
        binding.todayTaskRecyclerview.layoutManager = taskLayoutManager


        // 3
        // setup data in project adapter
        projectListAdapter =
            HomeProjectListAdapter(object : HomeProjectListAdapter.OnItemClickListener {
                override fun onItemClick(project: Project, view: View) {

                    exitTransition = MaterialElevationScale(false).apply {
                        duration = 400
                    }
                    reenterTransition = MaterialElevationScale(true).apply {
                        duration = 400
                    }

                    val extras = FragmentNavigatorExtras(view to "project_fragment")
                    val bundle = Bundle().apply {
                        putLong("project_id", project.projectId)
                    }

                    findNavController().navigate(
                        R.id.action_homeFragment_to_projectFragment,
                        bundle,
                        null,
                        extras
                    )
                }
            })
        val projectLayoutManager = LinearLayoutManager(contextApp)
        binding.allProjectRecyclerView.adapter = projectListAdapter
        binding.allProjectRecyclerView.layoutManager = projectLayoutManager
        val deleteIcon = ContextCompat.getDrawable(contextApp, R.drawable.icon_delete)
        val itemDeleteHelper = ItemDeleteHelper(deleteIcon!!, ContextCompat.getColor(contextApp, R.color.red_600)){

            Toast.makeText(contextApp, it.toString(), Toast.LENGTH_SHORT).show()
        }
        val itemTouchHelper = itemDeleteHelper.setup(binding.allProjectRecyclerView)


        // 4
        chipsAdapter = CustomChipListAdapter(binding.chipsContainer, contextApp, object : CustomChipListAdapter.OnItemClickListener{
            override fun onItemClick(chipData: ChipData, itemIndex: Int) {
                selectedChipIndex = itemIndex
                selectedCollectionId = collectionNames.getCollectionId(chipData.name)
                updateChips()
                updateRecyclerView()

            }

            override fun onIconClick() {
                // TODO add new collection
                findNavController().navigate(R.id.action_homeFragment_to_collectionFragment)
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
            mainActivityViewModel.spManager.getCollection().collect {
                collectionNames = it
                updateChips()
            }
        }

        lifecycleScope.launch{
            mainActivityViewModel.getAllTodayTasks(dateTimeManager.getTomorrowDate()).collect{
                val filteredTask = ArrayList<ProjectTask>()
                for (task in it){
                    if(task.taskStatus != TaskStatus.Done){
                        filteredTask.add(task)
                    }
                }

                binding.todoLabel.text = filteredTask.size.toString()
                taskListAdapter.submitList(filteredTask)
            }
        }
    }


    private fun updateChips(){
        val list = ArrayList<ChipData>()
        for(index in collectionNames.indices){

            val isActive = index == selectedChipIndex
            val count = if(collectionNames[index].name == "All"){
                allProjects.size
            }else{
                allProjects.countCollection(collectionNames[index].id)
            }
            list.add(ChipData(index, collectionNames[index].name, count, isActive))
        }
        chipsAdapter.submitList(list)

    }

    private fun updateRecyclerView(){

        val filteredProject = ArrayList<Project>()
        if(selectedCollectionId != 1234567L){

            for (project in allProjects){
                if(project.collectionId == selectedCollectionId){
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

