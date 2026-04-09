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

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        auth = FirebaseAuth.getInstance()

        // If already logged in, skip straight to MainActivity
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val emailField = findViewById<EditText>(R.id.etEmail)
        val passwordField = findViewById<TextInputEditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signUpText = findViewById<TextView>(R.id.tvSignUp)

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                loginButton.isEnabled = false
                loginButton.text = getString(R.string.signing_in)
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        loginButton.isEnabled = true
                        loginButton.text = getString(R.string.sign_in)
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            val errorCode = (task.exception as? com.google.firebase.auth.FirebaseAuthException)?.errorCode
                            val friendlyMessage = when (errorCode) {
                                "ERROR_WRONG_PASSWORD",
                                "ERROR_INVALID_CREDENTIAL" -> "Incorrect password. Please try again."
                                "ERROR_USER_NOT_FOUND"     -> "No account found with this email."
                                "ERROR_INVALID_EMAIL"      -> "Please enter a valid email address."
                                "ERROR_USER_DISABLED"      -> "This account has been disabled."
                                "ERROR_TOO_MANY_REQUESTS"  -> "Too many attempts. Please try again later."
                                else                       -> "Login failed. Please check your details."
                            }
                            Toast.makeText(this, friendlyMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }
}