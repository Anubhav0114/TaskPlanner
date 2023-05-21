package com.example.taskplanner

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.taskplanner.databinding.ActivityMainBinding
import com.example.taskplanner.viewmodel.MainActivityViewModel
import com.example.taskplanner.viewmodel.MainActivityViewModelFactory
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
                    val intent = Intent(this , SignIn::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            true
        }


    }


    fun openNavDrawer(){
        binding.drawerLayout.open()
    }
}