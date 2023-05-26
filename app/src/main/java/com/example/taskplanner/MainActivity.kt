package com.example.taskplanner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.taskplanner.databinding.ActivityMainBinding
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var auth : FirebaseAuth

    private val mainActivityViewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(
            (application as ProjectApplication).projectRepository,
            (application as ProjectApplication).taskRepository
        )
    }

    private val appLink = "https://play.google.com/store/apps/details?id=com.flaxstudio.drawon"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mainActivityViewModel.setupViewModel(applicationContext)
        auth = Firebase.auth


//        val intent = Intent(this ,SignIn::class.java )
//        startActivity(intent)
        binding.navView.setNavigationItemSelectedListener { menuItem ->

            when(menuItem.itemId){
                R.id.notification_item -> {
                    Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_LONG).show()
                }
                R.id.about_item -> {
                    Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_LONG).show()
                }
                R.id.themeItem->{themeChange()}
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

                R.id.logOut_btn -> {
                    auth.signOut()
                    val googleSignInClient = GoogleSignIn.getClient(this , GoogleSignInOptions.DEFAULT_SIGN_IN)
                    googleSignInClient.signOut().addOnCompleteListener{
                        val intent = Intent(this , SignIn::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }

            true
        }


    }


    @SuppressLint("MissingInflatedId")
    private fun themeChange() {
        val themeOptions = arrayOf("Purple Theme", "Red Theme", "Green Theme", "Orange Theme")

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_theme_change, null)

        builder.setView(dialogView)
        builder.setTitle("Change App Theme")

        val listView = dialogView.findViewById<ListView>(R.id.list_theme_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, themeOptions)
        listView.adapter = adapter

        val dialog = builder.create()
        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    // Apply purple theme
                    recreate() // Recreate the activity to apply the new theme
                    setTheme(R.style.Theme_purple)
                }
                1 -> {
                    // Apply red theme
                    recreate() // Recreate the activity to apply the new theme
                    setTheme(R.style.Theme_red)
                }
                2 -> {
                    // Apply green theme
                    recreate() // Recreate the activity to apply the new theme
                    setTheme(R.style.Theme_green)
                }
                3 -> {
                    // Apply orange theme
                    recreate() // Recreate the activity to apply the new theme
                    setTheme(R.style.Theme_orange)
                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }
    fun openNavDrawer(){
        binding.drawerLayout.open()
    }
}