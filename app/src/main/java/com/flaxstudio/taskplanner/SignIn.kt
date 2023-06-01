package com.flaxstudio.taskplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.flaxstudio.taskplanner.databinding.ActivitySignInBinding
import com.flaxstudio.taskplanner.room.UserDao
import com.flaxstudio.taskplanner.room.Users
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModel
import com.flaxstudio.taskplanner.viewmodel.MainActivityViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


private const val TAG = "SignIn"
class SignIn : AppCompatActivity() {

    private  var RC_SIGN_IN : Int = 123
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    private val mainActivityViewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(
            (application as ProjectApplication).projectRepository,
            (application as ProjectApplication).taskRepository
        )
    }

    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainActivityViewModel.setupViewModel(applicationContext)

        val text = "Don't have an Account? <font color=#1E7EE4>Register</font>"
        binding.registerTextview.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

        // Configure Google Sign In

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth



        binding.buttonGoogle.setOnClickListener{
            val  signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)

            }

        binding.registerTextview.setOnClickListener {
            val intent = Intent(this , SignUpActivity::class.java)
            startActivity(intent)

        }

        binding.buttonLogin.setOnClickListener {
            val email  = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Bad Credentials" , Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                authenticateWithEmail(email, password)
            }
        }



        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
                result ->
            if (result.resultCode == RESULT_OK){
                val data : Intent? = result.data
                // Pass the result data to the Firebase Auth method for handling the sign-in
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                Log.i(TAG, "Inside the Sign In Intent")

                try {
                    // Google sign-in was successful, authenticate with Firebase

                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account?.idToken)
                } catch (e: ApiException) {
                    // Google sign-in failed, handle the error
                    Log.e(TAG, "Google sign-in failed", e)
                    // Show an error message or handle the failed sign-in
                }

            }else{
                // Google sign-in was canceled or failed, handle accordingly
                Log.e(TAG, "Google sign-in Intent failed")
            }
        }



    }

    private fun authenticateWithEmail(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUi(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "No such user found",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUi(null)
                }
            }

    }

    override fun onStart() {
        super.onStart()
        val currUser = auth.currentUser
        updateUi(currUser)
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {

        val credential = GoogleAuthProvider.getCredential(idToken , null)

        binding.buttonGoogle.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.progressTv.visibility = View.VISIBLE
        binding.container.isClickable = false

        GlobalScope.launch(Dispatchers.IO){
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            val users = Users( firebaseUser?.displayName.toString() , firebaseUser?.photoUrl.toString() , firebaseUser!!.uid )
            val userDao = UserDao()
            userDao.addUser(users)
            withContext(Dispatchers.Main){
                updateUi(firebaseUser)
            }
        }
    }

    private fun updateUi(firebaseUser: FirebaseUser?) {

        if (firebaseUser != null){
//            val user = User(firebaseUser.uid , firebaseUser.displayName.toString() , firebaseUser.photoUrl.toString())
//            val userDao = UserDao()
//            userDao.addUser(user)
            mainActivityViewModel.getSyncData {
                Log.d(TAG, "updateUi: ${it}")
                val jsonRef = Firebase.storage.reference.child("${firebaseUser.uid}.json")
                jsonRef.putBytes(it.toByteArray()).addOnCompleteListener {
                    Log.d(TAG, "updateUi: data added successfully")
                }.addOnFailureListener {
                    Log.d(TAG, "updateUi: error:${it.toString()}")
                }
            }
            val mainActivityIntent = Intent(this , com.flaxstudio.taskplanner.MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }else{
            binding.buttonGoogle.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.progressTv.visibility = View.GONE
            binding.container.isClickable = true
        }

    }


}