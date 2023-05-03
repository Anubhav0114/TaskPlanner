package com.example.taskplanner.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.taskplanner.R
import com.example.taskplanner.databinding.FragmentCollectionBinding

class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

}