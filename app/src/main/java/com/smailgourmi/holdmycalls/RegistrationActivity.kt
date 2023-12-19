package com.smailgourmi.holdmycalls

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
/*

class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth;
    private lateinit var mDbRef: DatabaseReference
    private lateinit var signupFullName:EditText;
    private lateinit var signupPhoneNumber: EditText;
    private lateinit var signupEmail : EditText ;
    private lateinit var signupPassword : EditText;
    private  lateinit var signupButton : Button;
    private lateinit var  loginRedirectText : TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val firebaseApp = FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        signupFullName = findViewById(R.id.signup_full_name)
        signupPhoneNumber=findViewById(R.id.signup_phone_number)
        signupEmail = findViewById<EditText>(R.id.signup_email)
        signupPassword = findViewById<EditText>(R.id.signup_password)
        signupButton = findViewById<Button>(R.id.signup_button)
        loginRedirectText = findViewById<TextView>(R.id.loginRedirectText)
        signupButton.setOnClickListener{

                val email: String = signupEmail.text.toString().trim()
                val pass: String = signupPassword.text.toString().trim()
                val full_name :String = signupFullName.text.toString()
                val phone_number:String = signupPhoneNumber.text.toString()
                if (email.isEmpty()) {
                    signupEmail.error = "Email cannot be empty"
                }
                if (pass.isEmpty()) {
                    signupPassword.error = "Password cannot be empty"
                }
                if(phone_number.isEmpty()){
                    signupPhoneNumber.error="Phone Number cannot be empty"
                }
                if(full_name.isEmpty()){
                    signupFullName.error="Name must not be empty"
                }
                else {
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                        OnCompleteListener<AuthResult?> { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "SignUp Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                AddUserToDatabase(full_name,phone_number,email, auth.currentUser?.uid!!)
                                startActivity(
                                    Intent(
                                        this@RegistrationActivity,
                                        LoginActivity::class.java
                                    )
                                )
                            } else {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "SignUp Failed" + task.exception!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
        }
        loginRedirectText.setOnClickListener{
            startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
        }
    }

    private fun AddUserToDatabase(fullName: String, phoneNumber: String,email:String, uid: String) {

        mDbRef = FirebaseDatabase.getInstance(getString(R.string.database_reference)).reference
        val userIdReference = mDbRef.child("users").child(uid)
        userIdReference.setValue(User(fullName,phoneNumber,email,uid))
    }
}
*/