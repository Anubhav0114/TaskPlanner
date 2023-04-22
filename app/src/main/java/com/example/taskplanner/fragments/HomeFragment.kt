package com.example.taskplanner.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
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


        binding.newProjectBtn.setOnClickListener {
            // Alert Box
            dialog()
        }
        //findNavController().navigate(R.id.action_homeFragment_to_projectFragment)
    }

    private fun dialog(){
        val dialogView = LayoutInflater.from(contextApp).inflate(R.layout.custom_dialog, null)
        val builder = AlertDialog.Builder(contextApp)
        builder.setView(dialogView)

        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.dialog_ok)?.setOnClickListener {
            // handle OK button click
            Toast.makeText(contextApp,"Add Project To Db",Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.dialog_cancel)?.setOnClickListener {
            // handle Cancel button click
            Toast.makeText(contextApp,"Close It",Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialog.show()

    }
}