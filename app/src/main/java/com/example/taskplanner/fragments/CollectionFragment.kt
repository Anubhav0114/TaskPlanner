package com.example.taskplanner.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.adapters.CollectionListAdapter
import com.example.taskplanner.databinding.FragmentCollectionBinding
import com.example.taskplanner.room.Project
import com.example.taskplanner.utils.ChipData
import com.example.taskplanner.utils.CollectionData
import com.example.taskplanner.utils.SharedPreferenceManager
import com.example.taskplanner.utils.countCollection
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import kotlinx.coroutines.launch

class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding
    private lateinit var collectionListAdapter: CollectionListAdapter
    private lateinit var contextApp: Context
    private lateinit var spManager: SharedPreferenceManager
    private var allProjects = emptyList<Project>()
    private var collectionNames = emptyList<String>()


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
        // Inflate the layout for this fragment
        binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contextApp = requireContext()
        setupUi()
        addObserver()
    }


    private fun setupUi(){
        spManager = SharedPreferenceManager(lifecycleScope, contextApp)

        collectionListAdapter = CollectionListAdapter(object : CollectionListAdapter.OnItemClickListener{
            override fun onMenuClick(data: CollectionData, view: View) {

            }

        })
        binding.collectionRecyclerview.adapter = collectionListAdapter
        binding.collectionRecyclerview.layoutManager = LinearLayoutManager(contextApp)
    }

    private fun addObserver(){
        lifecycleScope.launch {
            mainActivityViewModel.getAllProjects().collect {
                allProjects = it
                updateCollection()
            }
        }

        lifecycleScope.launch {
            spManager.getCollection().collect {
                collectionNames = it
                updateCollection()
            }
        }
    }


    private fun updateCollection(){
        val list = ArrayList<CollectionData>()
        for(index in collectionNames.indices){

            val count = if(collectionNames[index] == "All"){
                allProjects.size
            }else{
                allProjects.countCollection(collectionNames[index])
            }
            list.add(CollectionData(collectionNames[index], count))
        }
        collectionListAdapter.submitList(list)
    }
}