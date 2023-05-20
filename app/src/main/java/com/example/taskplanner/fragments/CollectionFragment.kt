package com.example.taskplanner.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
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
import com.example.taskplanner.utils.CollectionRawData
import com.example.taskplanner.utils.SharedPreferenceManager
import com.example.taskplanner.utils.countCollection
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding
    private lateinit var collectionListAdapter: CollectionListAdapter
    private lateinit var contextApp: Context
    private lateinit var spManager: SharedPreferenceManager
    private var allProjects = emptyList<Project>()
    private var collectionNames = emptyList<CollectionRawData>()


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

            override fun onRenameClick(data: CollectionData) {
                showRenameDialog(data)
            }

            override fun onDeleteClick(data: CollectionData) {
               showDeleteDialog(data)
            }

        })
        binding.createCollection.setOnClickListener {
            createCollectionDialog()
        }

        binding.collectionRecyclerview.adapter = collectionListAdapter
        binding.collectionRecyclerview.layoutManager = LinearLayoutManager(contextApp)
    }

    private var isUpdateAllowed = false
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
                if(isUpdateAllowed){
                    updateCollection()
                }

                isUpdateAllowed = true

            }
        }
    }


    private fun updateCollection(){
        val list = ArrayList<CollectionData>()
        for(index in collectionNames.indices){

            val count = if(collectionNames[index].name == "All"){
                allProjects.size
            }else{
                allProjects.countCollection(collectionNames[index].id)
            }
            list.add(CollectionData(collectionNames[index].id, collectionNames[index].name, count))
        }
        collectionListAdapter.submitList(list)
    }


    private fun showDeleteDialog(collectionData: CollectionData){
        MaterialAlertDialogBuilder(contextApp)
            .setTitle("Delete Collection")
            .setMessage("Do you really want to delete '${collectionData.name}'? This will delete all projects from this collection!")

            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->

            }
            .setPositiveButton(resources.getString(R.string.delete)) { dialog, which ->
                spManager.removeCollectionItem(collectionData.id)
            }
            .show()
    }


    private fun showRenameDialog(collectionData: CollectionData){
        val dialogView = LayoutInflater.from(contextApp).inflate(R.layout.one_input_dialog, null)
        val editTextInput = dialogView.findViewById<EditText>(R.id.editText).apply {
            setText(collectionData.name)
        }

        val dialog = MaterialAlertDialogBuilder(contextApp)
            .setTitle("Rename Collection")
            .setView(dialogView)
            .setPositiveButton("Rename") { dialog, which ->
                val userInput = editTextInput.text.toString()
                // Handle the user input here
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun createCollectionDialog(){
        val dialogView = LayoutInflater.from(contextApp).inflate(R.layout.one_input_dialog, null)
        val editTextInput = dialogView.findViewById<EditText>(R.id.editText)

        val dialog = MaterialAlertDialogBuilder(contextApp)
            .setTitle("Create Collection")
            .setView(dialogView)
            .setPositiveButton("Create") { dialog, which ->
                val userInput = editTextInput.text.toString().trim()
                if(userInput.isBlank()){
                    Toast.makeText(contextApp, "Collection name must not be empty!", Toast.LENGTH_SHORT).show()
                }else if(spManager.isCollectionExist(userInput)){
                    Toast.makeText(contextApp, "Collection name already exist!", Toast.LENGTH_SHORT).show()
                }else{
                    spManager.addCollectionItem(userInput)
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}