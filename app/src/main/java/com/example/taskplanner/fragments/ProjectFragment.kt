package com.example.taskplanner.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.databinding.FragmentProjectBinding
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory


class ProjectFragment : Fragment() {


    private lateinit var binding: FragmentProjectBinding

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
        binding = FragmentProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contextApp = requireContext()

        //findNavController().navigate(R.id.action_projectFragment_to_taskFragment)
        binding.editTask.setOnClickListener {
            dialog()
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun dialog(){
        val dialogView = LayoutInflater.from(contextApp).inflate(R.layout.add_task_dialog, null)
        val builder = AlertDialog.Builder(contextApp)
        builder.setView(dialogView)

        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.dialog_create)?.setOnClickListener {
            // handle OK button click
            Toast.makeText(contextApp,"Add Task To Db", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.dialog_cancel)?.setOnClickListener {
            // handle Cancel button click
            Toast.makeText(contextApp,"Close It", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialog.show()

    }
}