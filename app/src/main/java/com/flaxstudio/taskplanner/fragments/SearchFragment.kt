package com.flaxstudio.taskplanner.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.flaxstudio.taskplanner.ProjectApplication
import com.flaxstudio.taskplanner.R
import com.flaxstudio.taskplanner.adapters.SearchListAdapter
import com.flaxstudio.taskplanner.databinding.FragmentSearchBinding
import com.flaxstudio.taskplanner.room.Project
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModel
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.Job


class SearchFragment : Fragment() {

    private var previousStatusBarColor: Int? = null
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
        previousStatusBarColor = activity?.window?.statusBarColor
        super.onViewCreated(view, savedInstanceState)

        // handle animations
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        activity?.window?.statusBarColor = Color.WHITE

        contextApp = requireContext()
        setupUI()
        addListeners()
        searchProject("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        previousStatusBarColor?.let {
            activity?.window?.statusBarColor = it
        }
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
        binding.resultRecyclerview.itemAnimator?.apply {
            addDuration = 0
            changeDuration = 0
            moveDuration = 0
            removeDuration = 0
        }


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
            if(it.isEmpty()){
                binding.resultTextview.visibility = View.VISIBLE
            }else{
                binding.resultTextview.visibility = View.INVISIBLE
            }
        }
    }


}