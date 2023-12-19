package com.smailgourmi.holdmycalls

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.smailgourmi.holdmycalls.ui.main.MainActivity

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val signupButton: Button = findViewById(R.id.signupButton)
        val loginButton: Button = findViewById(R.id.loginButton)
        val signoutButton: Button = findViewById(R.id.signoutButton)

        // Sign Up Button Click
        signupButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign up success
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign up fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }

        // Log In Button Click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }

        // Sign Out Button Click
        signoutButton.setOnClickListener {
            auth.signOut()
            updateUI(null)
        }
    }

    private fun updateUI(user: Any?) {
        // Update UI based on user authentication status
        if (user != null) {
            // User is signed in
            Toast.makeText(baseContext, "Authentication successful.",
                Toast.LENGTH_SHORT).show()
            navigateToMainActivity()
        } else {
            // User is signed out
            Toast.makeText(baseContext, "User signed out.",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMainActivity() {
        // Example: If the user is authenticated, navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
