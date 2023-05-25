package com.example.taskplanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.taskplanner.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.buttonSubmit.setOnClickListener {
            if (binding.emailEt.text.toString().isNotEmpty()){
                auth.sendPasswordResetEmail(binding.emailEt.text.toString())
                    .addOnCompleteListener {task->
                        if (task.isSuccessful) Toast.makeText(this,
                            "Email Sent to ${binding.emailEt.text.toString()}",
                            Toast.LENGTH_LONG).show()
                        else Toast.makeText(this,
                            "${task.exception}",
                            Toast.LENGTH_LONG).show()
                    }
            }else binding.emailEt.error = "Enter a mail to proceed"
        }
    }
}