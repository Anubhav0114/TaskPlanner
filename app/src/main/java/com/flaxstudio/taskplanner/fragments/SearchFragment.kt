package com.flaxstudio.taskplanner.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.adapters.SearchListAdapter
import com.example.taskplanner.databinding.FragmentSearchBinding
import com.example.taskplanner.room.Project
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.Job


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private var searchJob: Job? = null
    private lateinit var searchListAdapter: SearchListAdapter

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(
            (requireActivity().application as ProjectApplication).projectRepository,
            (requireActivity().application as ProjectApplication).taskRepository
        )
    }
    private lateinit var contextApp: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // used to handle back press
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // handle animations
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        contextApp = requireContext()
        setupUI()
        addListeners()
        searchProject("")
    }


    private fun setupUI(){
        searchListAdapter = SearchListAdapter(object : SearchListAdapter.OnItemClickListener{
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
                    R.id.action_searchFragment_to_projectFragment,
                    bundle,
                    null,
                    extras
                )
            }
        })

        binding.resultRecyclerview.adapter = searchListAdapter
        binding.resultRecyclerview.layoutManager = LinearLayoutManager(contextApp, LinearLayoutManager.VERTICAL, false)


        // 2
        binding.searchEditText.addTextChangedListener {
            searchProject(it.toString())
        }
    }


    private fun addListeners(){
        binding.backBtn.setOnClickListener {
            // back to previous
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun searchProject(query: String){
        searchListAdapter.searchQuery = query

        if(searchJob != null && searchJob!!.isActive){
            searchJob!!.cancel()
        }

        searchJob = mainActivityViewModel.searchProject(query){
            searchListAdapter.submitList(it)
        }
    }


}