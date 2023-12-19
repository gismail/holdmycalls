package com.smailgourmi.holdmycalls

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.smailgourmi.holdmycalls.ui.main.MainActivity

/*
class LoginActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        var loginEmail: EditText? = null
        var loginPassword: EditText? = null
        var signupRedirectText: TextView? = null
        var loginButton: Button? = null
        var forgotPassword: TextView? = null
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginEmail = findViewById<EditText>(R.id.login_email)
        loginPassword = findViewById<EditText>(R.id.login_password)
        loginButton = findViewById<Button>(R.id.login_button)
        signupRedirectText = findViewById<TextView>(R.id.signUpRedirectText)
        forgotPassword = findViewById<TextView>(R.id.forgot_password)
        auth = FirebaseAuth.getInstance()
        loginButton.setOnClickListener{
                val email = /*loginEmail.text.toString() ||*/ "smailgourmi@gmail.com"
                val pass =/* loginPassword.text.toString() ||*/ "12345678"
                if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (pass.isNotEmpty()) {
                        auth!!.signInWithEmailAndPassword(email, pass)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    )
                                )
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        loginPassword.error = "Empty fields are not allowed"
                    }
                } else if (email.isEmpty()) {
                    loginEmail.error = "Empty fields are not allowed"
                } else {
                    loginEmail.error = "Please enter correct email"
                }

        }
        signupRedirectText.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
        }
        forgotPassword.setOnClickListener{
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@LoginActivity)
                val dialogView: View = layoutInflater.inflate(R.layout.dialog_forgot, null)
                val emailBox: EditText = dialogView.findViewById(R.id.emailBox)
                builder.setView(dialogView)
                val dialog: AlertDialog = builder.create()
                dialogView.findViewById<Button>(R.id.btnReset)
                    .setOnClickListener{
                            val userEmail = emailBox.text.toString()
                            if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(
                                    userEmail
                                ).matches()
                            ) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Enter your registered email id",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            auth!!.sendPasswordResetEmail(userEmail).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Check your email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dialog.dismiss()
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Unable to send, failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                    }
                dialogView.findViewById<Button>(R.id.btnCancel)
                    .setOnClickListener{
                        dialog.dismiss()
                    }
                if (dialog.window != null) {
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                }
                dialog.show()
        }
    }
}
*/