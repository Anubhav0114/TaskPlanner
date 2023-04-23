package com.example.taskplanner.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.taskplanner.ProjectApplication
import com.example.taskplanner.R
import com.example.taskplanner.databinding.FragmentHomeBinding
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.play.core.review.ReviewManagerFactory


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private  lateinit var toggle:ActionBarDrawerToggle
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(
            (requireActivity().application as ProjectApplication).projectRepository,
            (requireActivity().application as ProjectApplication).taskRepository
        )
    }
    private lateinit var contextApp: Context
    private val appLink = "https://play.google.com/store/apps/details?id=com.flaxstudio.drawon"


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

        toggle = ActionBarDrawerToggle(contextApp as Activity?,binding.drawerLayout,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navButton.setOnClickListener {
            binding.drawerLayout.open()
        }

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.notification_item -> {
                    Toast.makeText(contextApp, "Clicked", Toast.LENGTH_LONG).show()
                }
                R.id.about_item -> {
                    Toast.makeText(contextApp, "Clicked", Toast.LENGTH_LONG).show()
                }
                R.id.feedback_item -> {
                    val intent = Intent().apply{
                        action = Intent.ACTION_SENDTO
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("flaxstudiohelp@gmail.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "Tell about our application")
                    }
                    startActivity(Intent.createChooser(intent, "Send Email"))
                }
                R.id.rating_item -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(appLink))
                    startActivity(browserIntent)
                }
                R.id.shareItem -> {
                    val sendIntent : Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT , "Hey, I just made a really cool Task using Task Planner App .You should also download this amazing App.")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent , "Share Task Planner to your Friends")
                    startActivity(shareIntent)
                }
                R.id.moreApps_item -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://flax-studio.vercel.app"))
                    startActivity(browserIntent)
                }
            }
            true
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

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    private fun dialog(){
        val dialogView = LayoutInflater.from(contextApp).inflate(R.layout.add_project_dialog, null)
        val builder = AlertDialog.Builder(contextApp)
        builder.setView(dialogView)

        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.dialog_ok)?.setOnClickListener {
            // handle OK button click
            Toast.makeText(contextApp,"Add Project To Db",Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_homeFragment_to_projectFragment)
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