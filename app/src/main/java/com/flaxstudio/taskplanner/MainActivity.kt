package com.flaxstudio.taskplanner

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.flaxstudio.taskplanner.databinding.ActivityMainBinding
import com.flaxstudio.taskplanner.room.Users
import com.flaxstudio.taskplanner.utils.ThemeManager
import com.flaxstudio.taskplanner.utils.removeSurname
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModel
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {

    //  0= Theme_blue, 1= Theme_red, 2= Theme_purple, 3= Theme_orange,4= Theme_green
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userName: TextView
    private lateinit var userImage: ImageView
    val TAG = "MainActivity"

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

        val syncedData = intent.getStringExtra("synced data")
        if(syncedData != null){
            mainActivityViewModel.saveSyncData(syncedData){isSuccess ->
                if(!isSuccess){
                    Toast.makeText(applicationContext, "Something went wrong in fetching data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        auth = Firebase.auth


        val navView = binding.navView.getHeaderView(0)
        userName = navView.findViewById(R.id.NavUserName)
        userImage = navView.findViewById(R.id.UserImage)

        mainActivityViewModel.getSyncData {
            Log.e("MainActivity", it)
        }



        updateNameAndImage()


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
                R.id.syncData_item -> {
                    mainActivityViewModel.getSyncData { it ->
                        Log.d(TAG, "updateUi: $it")

                        val jsonRef =
                            Firebase.storage.reference.child("${auth.currentUser!!.uid}.json")


                        jsonRef.putBytes(it.toByteArray()).addOnCompleteListener {
                            Log.d(TAG, "updateUi: data added successfully")
                            Toast.makeText(this , "data backed up successfully" ,Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Log.d(TAG, "updateUi: error:${it.toString()}")
                            Toast.makeText(this , "data back up unsuccessful" ,Toast.LENGTH_SHORT).show()

                        }


                        // save the data at firebase realtime database



                    }
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
        val savedThemeMode = ThemeManager.getSavedThemeMode(this)
        ThemeManager.applyTheme(this, savedThemeMode)
    }

    private fun themeChange() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(applicationContext)
        val customView = inflater.inflate(R.layout.theme_picker_dialog, binding.root, false)
        builder.setView(customView)
        val dialog = builder.create()

        customView.findViewById<ShapeableImageView>(R.id.blue).setOnClickListener {
            ThemeManager.applyTheme(this, ThemeManager.ThemeMode.BLUE)
            openRestartSnackBar()
            dialog.dismiss()
        }

        customView.findViewById<ShapeableImageView>(R.id.orange).setOnClickListener {
            ThemeManager.applyTheme(this, ThemeManager.ThemeMode.ORANGE)
            openRestartSnackBar()
            dialog.dismiss()
        }

        customView.findViewById<ShapeableImageView>(R.id.green).setOnClickListener {
            ThemeManager.applyTheme(this, ThemeManager.ThemeMode.GREEN)
            openRestartSnackBar()
            dialog.dismiss()
        }

        customView.findViewById<ShapeableImageView>(R.id.red).setOnClickListener {
            ThemeManager.applyTheme(this, ThemeManager.ThemeMode.RED)
            openRestartSnackBar()
            dialog.dismiss()
        }

        customView.findViewById<ShapeableImageView>(R.id.purple).setOnClickListener {
            ThemeManager.applyTheme(this, ThemeManager.ThemeMode.PURPLE)
            openRestartSnackBar()
            dialog.dismiss()
        }

        dialog.show()

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
                if(user.imageUrl.isBlank()){
                    Glide.with(userImage.context).load(R.drawable.women_icon).circleCrop().into(userImage)
                }else{
                    Glide.with(userImage.context).load(user.imageUrl).circleCrop().into(userImage)
                }
            }
        }.addOnFailureListener {
            Log.i("MainActivity", " Failure while fetching the user")
        }
    }

    private fun openRestartSnackBar(){

        val snackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT)
        val customSnackBarLayout: View = layoutInflater.inflate(R.layout.undo_snakbar, snackBar.view as ViewGroup, false)
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
        snackBarLayout.setPadding(0, 0, 0, 0)
        val snakebite = "Restart the application for seeing the change"
        customSnackBarLayout.findViewById<TextView>(R.id.message).text = snakebite
        val snarkButton = "Restart"
        customSnackBarLayout.findViewById<Button>(R.id.button).text =snarkButton
        customSnackBarLayout.findViewById<Button>(R.id.button).setOnClickListener {
            snackBar.dismiss()
            finish()
            startActivity(intent)
        }
        snackBarLayout.addView(customSnackBarLayout, 0)
        snackBar.show()
    }

}