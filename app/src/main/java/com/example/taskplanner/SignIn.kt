package com.example.taskplanner

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global
import android.text.Html
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.taskplanner.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


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
                    Log.e("SignInActivity", "Google sign-in failed", e)
                    // Show an error message or handle the failed sign-in
                }

            }else{
                // Google sign-in was canceled or failed, handle accordingly
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

        GlobalScope.launch(Dispatchers.IO){
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main){
                updateUi(firebaseUser)
            }
        }
    }

    private fun updateUi(firebaseUser: FirebaseUser?) {

        if (firebaseUser != null){
            val user = User(firebaseUser.uid , firebaseUser.displayName.toString() , firebaseUser.photoUrl.toString())
            val userDao = UserDao()
            userDao.addUser(user)
            val mainActivityIntent = Intent(this , MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }else{
            binding.buttonGoogle.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }

    }


}