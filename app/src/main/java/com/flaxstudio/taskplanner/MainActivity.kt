package com.flaxstudio.taskplanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.flaxstudio.taskplanner.databinding.ActivityMainBinding
import com.flaxstudio.taskplanner.room.Users
import com.flaxstudio.taskplanner.utils.removeSurname
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModel
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    //  0= Theme_blue, 1= Theme_red, 2= Theme_purple, 3= Theme_orange,4= Theme_green
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userName: TextView
    private lateinit var userImage: ImageView

    private val mainActivityViewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(
            (application as ProjectApplication).projectRepository,
            (application as ProjectApplication).taskRepository
        )
    }

    private val appLink = "https://play.google.com/store/apps/details?id=com.flaxstudio.drawon"
    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mainActivityViewModel.setupViewModel(applicationContext)
        auth = Firebase.auth


        val navView = binding.navView.getHeaderView(0)
        userName = navView.findViewById(R.id.NavUserName)
        userImage = navView.findViewById(R.id.UserImage)

        mainActivityViewModel.getSyncData {
            Log.e("MainActivity", it)
        }



        updateNameAndImage()


//        val intent = Intent(this ,SignIn::class.java )
//        startActivity(intent)
//        val v: View = binding.navView.getHeaderView(0)
//        val text:TextView = v.findViewById(R.id.textView11)
//        text.text = "Hi Sayam"
        binding.navView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.notification_item -> {
                    Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_LONG).show()
                }
                R.id.about_item -> {
                    Toast.makeText(applicationContext, "Clicked", Toast.LENGTH_LONG).show()
                }
                R.id.themeItem -> {
                    themeChange()
                }
                R.id.feedback_item -> {
                    val intent = Intent().apply {
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
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Hey, I just made a really cool Task using Task Planner App .You should also download this amazing App."
                        )
                        type = "text/plain"
                    }
                    val shareIntent =
                        Intent.createChooser(sendIntent, "Share Task Planner to your Friends")
                    startActivity(shareIntent)
                }
                R.id.moreApps_item -> {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://flax-studio.vercel.app"))
                    startActivity(browserIntent)
                }

                R.id.logOut_btn -> {
                    auth.signOut()
                    val googleSignInClient =
                        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
                    googleSignInClient.signOut().addOnCompleteListener {
                        val intent = Intent(this, com.flaxstudio.taskplanner.SignIn::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }

            true
        }


    }

    private fun applySavedTheme() {
        val savedThemeMode = com.flaxstudio.taskplanner.ThemeManager.getSavedThemeMode(this)
        com.flaxstudio.taskplanner.ThemeManager.applyTheme(this, savedThemeMode)
    }

    private fun themeChange() {
        val themeNames = arrayOf("Blue", "Red", "Purple", "Orange", "Green")

        val currentThemeMode = com.flaxstudio.taskplanner.ThemeManager.getSavedThemeMode(this)

        val selectedThemeIndex = currentThemeMode.ordinal

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Theme")
            .setSingleChoiceItems(themeNames, selectedThemeIndex) { dialog, selection ->
                val selectedThemeMode = com.flaxstudio.taskplanner.ThemeManager.ThemeMode.values()[selection]
                com.flaxstudio.taskplanner.ThemeManager.applyTheme(this, selectedThemeMode)
                Snackbar.make(
                    binding.root,
                    "Restart the application for seeing the change",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Restart") {
                        // Responds to click on the action
                        finish()
                        startActivity(intent)
                    }
                    .show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    fun openNavDrawer() {
        binding.drawerLayout.open()
    }

    private fun updateNameAndImage() {
        val currUser = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(currUser!!).get().addOnSuccessListener { userSnapshot ->
            val user = userSnapshot.toObject(Users::class.java)
            if (user != null) {
                val name = "Hello, ${removeSurname(user.displayName)}"
                userName.text = name
                Glide.with(userImage.context).load(user.imageUrl).circleCrop().into(userImage)
            }
        }.addOnFailureListener {
            Log.i("MainActivity", " Failure while fetching the user")
        }
    }
}