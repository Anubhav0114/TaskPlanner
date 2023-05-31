package com.flaxstudio.taskplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.example.taskplanner.databinding.ActivitySignUpBinding
import com.example.taskplanner.room.Users
import com.example.taskplanner.room.UserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = "By signing up you're Agree to Our <font color=#1E7EE4>Terms & Conditions.</font>"
        binding.signUpTextview.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val text2 = "Joined us before? <font color=#1E7EE4>Login.</font>"
        binding.signUpLoginTextview.text = HtmlCompat.fromHtml(text2, HtmlCompat.FROM_HTML_MODE_LEGACY)


        auth = Firebase.auth


        binding.buttonSignUp.setOnClickListener{
            var email = binding.emailEt.text.toString()
            var password = binding.passwordEt.text.toString()
            var name = binding.nameInput.text.toString()
            if (email.isEmpty() || password.isEmpty() || name.isEmpty()){
                Toast.makeText(this , "Please enter a valid Input" , Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                addUser(email , password , name)
            }
        }

    }

    private fun addUser(email : String , password : String , name : String) {
        auth.createUserWithEmailAndPassword(email , password)
            .addOnCompleteListener(this){
                task ->
                if (task.isSuccessful){
                    val user = auth.currentUser

                    val usersData =
                        user?.let { Users(name , " " , it.uid) }
                    val userDao = UserDao()
                    userDao.addUser(usersData)

                    updateUi(user)

                }else{
                    Toast.makeText(this , "Sign Up Failed , Try again" , Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currUser = auth.currentUser
        updateUi(currUser)
    }

    private fun updateUi(firebaseUser: FirebaseUser?) {

        if (firebaseUser != null){
            val mainActivityIntent = Intent(this , com.flaxstudio.taskplanner.MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }

    }
}