package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        auth = FirebaseAuth.getInstance()

        val nameField = findViewById<EditText>(R.id.etName)
        val emailField = findViewById<EditText>(R.id.etEmail)
        val passwordField = findViewById<TextInputEditText>(R.id.etPassword)
        val signUpButton = findViewById<Button>(R.id.btnSignUp)
        val loginText = findViewById<TextView>(R.id.tvLogin)

        signUpButton.setOnClickListener {
            val name     = nameField.text.toString().trim()
            val email    = emailField.text.toString().trim()
            val password = passwordField.text.toString()

            when {
                name.isEmpty() ->
                    Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT).show()
                email.isEmpty() ->
                    Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show()
                password.isEmpty() ->
                    Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show()
                password.length < 6 ->
                    Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show()
                else -> {
                    signUpButton.isEnabled = false
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            signUpButton.isEnabled = true
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Account created! Please sign in.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                val errorCode = (task.exception as? com.google.firebase.auth.FirebaseAuthException)?.errorCode
                                val msg = when (errorCode) {
                                    "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists."
                                    "ERROR_INVALID_EMAIL"        -> "Please enter a valid email address."
                                    "ERROR_WEAK_PASSWORD"        -> "Password is too weak. Use at least 6 characters."
                                    else                         -> "Sign up failed. Please try again."
                                }
                                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}