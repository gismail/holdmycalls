package com.smailgourmi.holdmycalls


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

/*

class SlaveScreenActivity : AppCompatActivity() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val db = FirebaseFirestore.getInstance()
    private  lateinit var auth: FirebaseAuth;
    private lateinit var mDbRef: DatabaseReference
    private  lateinit var userRecyclerView: RecyclerView
    private lateinit var contactList: ArrayList<Contact>
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slave_screen)
        auth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance(getString(R.string.database_reference)).reference
        contactList = ArrayList()
        contactAdapter=ContactAdapter(this@SlaveScreenActivity,contactList)
        userRecyclerView = findViewById(R.id.userRecyclerView)

        //val mockContact = Contact("Ayoub Bentafat","+330668622640")
        //addContact(mockContact)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = contactAdapter
        auth.currentUser?.uid?.let {
            mDbRef.child("users").child(it).child("contacts")
                .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    contactList.clear()
                    for (postSnapshot in snapshot.children){
                        val currentContact = postSnapshot.getValue(Contact::class.java)
                        contactList.add(currentContact!!)
                    }
                    contactAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        listenForMessages()
    }

    private fun addContact(mockContact: Contact) {
        mDbRef.child("users").child(auth.currentUser!!.uid).child("contacts")
            .push().setValue(mockContact)
    }

    private fun connectToMasterWithRetry(
        progressBar: ProgressBar,
        statusTextView: TextView,
        maxRetries: Int = 3,
        retryDelayMillis: Long = 1000
    ) {
        progressBar.visibility = View.VISIBLE
        statusTextView.text = "Connecting to Master..."

        val connectionDocRef = db.collection("connections")
            .document("slaveToMasterConnection")

        var retryCount = 0

        fun connect() {
            connectionDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Document exists, update the "connected" field to true
                        connectionDocRef.update("connected", true)
                            .addOnSuccessListener {
                                progressBar.visibility = View.GONE
                                statusTextView.text = "Connection to Master established successfully"
                                sendMessage("this a message from slave to master ")
                            }
                            .addOnFailureListener { exception ->
                                handleConnectionError(progressBar, statusTextView)
                            }
                    } else {
                        // Document doesn't exist, handle as needed
                        handleConnectionError(progressBar, statusTextView)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the error while reading the document
                    if (retryCount < maxRetries) {
                        // Retry after delay
                        retryCount++
                        mainHandler.postDelayed({
                            connect()
                        }, retryDelayMillis)
                    } else {
                        // Maximum retries reached, go back to MainAppScreen
                        handleConnectionError(progressBar, statusTextView)
                    }
                }
        }

        // Start the initial connection attempt
        connect()
    }

    private fun handleConnectionError(progressBar: ProgressBar, statusTextView: TextView) {
        progressBar.visibility = View.GONE
        statusTextView.text = "Error connecting to Master"

        // Optionally, you can navigate back to MainAppScreen here
        // For example:
        val intent = Intent(this, MainAppActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Function to send a message
    private fun sendMessage(message: String) {
        val messageData = hashMapOf(
            "text" to message,
            "timestamp" to FieldValue.serverTimestamp(),
            "sender" to "slave" // Add sender information if needed
        )

        db.collection("messages")
            .add(messageData)
            .addOnSuccessListener { documentReference ->
                // Message sent successfully
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }
    // Function to listen for messages
    private fun listenForMessages() {
        db.collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                for (doc in snapshots!!) {
                    // Handle each new message
                    val messageText = doc.getString("text")
                    val sender = doc.getString("sender")
                    // Process the message as needed
                }
            }
    }


}
*/