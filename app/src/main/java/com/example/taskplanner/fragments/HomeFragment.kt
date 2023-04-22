package com.example.taskplanner.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.databinding.FragmentHomeBinding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contextApp = requireContext()

        binding.navButton.setOnClickListener {
            binding.drawerLayout.open()
        }

        mainActivityViewModel.getAllProjectsTask {
            Toast.makeText(contextApp, "Room Working", Toast.LENGTH_SHORT).show()
        }


        //findNavController().navigate(R.id.action_homeFragment_to_projectFragment)
    }
}