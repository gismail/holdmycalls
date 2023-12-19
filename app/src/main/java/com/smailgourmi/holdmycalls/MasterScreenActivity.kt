package com.smailgourmi.holdmycalls
/*
import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.smailgourmi.holdmycalls.data.db.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MasterScreenActivity : AppCompatActivity() {
    // Initialize Firebase
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var currentUser : User
    private val db = FirebaseFirestore.getInstance()
    private lateinit var mDbRef: DatabaseReference
    private lateinit var smsReceiver: com.smailgourmi.holdmycalls.sms.SmsReceiver
    private  lateinit var auth: FirebaseAuth;
    private  lateinit var  messageTextView : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master_screen)
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> goHome()
                R.id.logout -> logOut()
                R.id.menu_settings-> setSettings()

            }
            true
        }
        // Display a message or perform actions specific to the Master Screen
        // For example:
        // val messageTextView: TextView = findViewById(R.id.messageTextView)
        messageTextView =         findViewById<TextView>(R.id.messageTextView)
        messageTextView.text = "Hello, You are in the master account. Connect to the slave account."


        auth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance(getString(R.string.database_reference)).reference
        CoroutineScope(Dispatchers.Main).launch {
            currentUser = getCurrentUser(mDbRef, auth)!!
            processReadWriteSMS()
        }



    }

    private fun initBroadCastReceiver() {
        smsReceiver = com.smailgourmi.holdmycalls.sms.SmsReceiver(auth,mDbRef, context = this)
    }

    private fun processReadWriteSMS() {
        // Check and request SMS permissions if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.RECEIVE_SMS),
                REQUEST_SMS_PERMISSIONS
            )
        }else{
            initBroadCastReceiver()
            listenRealSMSMessages()
        }
        if (ContextCompat. checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),
                        MY_REQUEST_CODE
            )
        }else
        {
            listenSlaveSMSMessages()
        }
    }

    private fun listenRealSMSMessages() {
        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsReceiver, intentFilter)
    }

    private fun listenSlaveSMSMessages() {
        mDbRef.child(getString(R.string.chats)).child(auth.currentUser!!.uid ).child(getString(R.string.metadata))
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val metaData = snapshot.getValue(MetaData::class.java)
                    if (metaData != null) {
                        processSlaveSMS(metaData)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun processSlaveSMS(metaData: MetaData?) {
        val query: Query = mDbRef.child(getString(R.string.chats)).child(auth.currentUser!!.uid )
            .child(metaData!!.phoneNumber!!).child(getString(R.string.messages))
            .orderByChild("timestamp")
            .startAt(metaData.timestamp!!.toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    // Handle data
                    val sms = snapshot.getValue(SMSMessage::class.java)
                    messageTextView.text = sms?.sms.toString()
                    if(!sms?.phoneNumber.equals( currentUser.phoneNumber))
                        sendSMSmessage(sms)
                }
            }

            override fun onCancelled(error: DatabaseError) {


            }

        })
    }

    private fun sendSMSmessage(sms: SMSMessage?) {
        // on the below line we are creating a try and catch block
        try {
            // on below line we are initializing sms manager.
            //as after android 10 the getDefault function no longer works
            //so we have to check that if our android version is greater
            //than or equal toandroid version 6.0 i.e SDK 23
            val smsManager: SmsManager
            if (Build.VERSION.SDK_INT>=23) {
                //if SDK is greater that or equal to 23 then
                //this is how we will initialize the SmsManager
                smsManager = this.getSystemService(SmsManager::class.java)
            }
            else{
                //if user's SDK is less than 23 then
                //SmsManager will be initialized like this
                smsManager = SmsManager.getDefault()
            }

            // on below line we are sending text message.
            smsManager.sendTextMessage(sms?.phoneNumber, null, sms?.sms, null, null)

            // on below line we are displaying a toast message for message send,
            Toast.makeText(applicationContext, "Message Sent", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {

            // on catch block we are displaying toast message for error.
            Toast.makeText(applicationContext, "Please enter all the data.."+e.message.toString(), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun goHome() {
        val intent = Intent(this, MainAppActivity::class.java)
        startActivity(intent)
    }

    private fun setSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun  logOut(): Boolean {
        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser!= null){
            auth.signOut()
        }
        // Redirect to the login or main screen after logout
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() //
        return true
    }

    private fun listenForSlaveConnection() {
        // Listen for changes in the "connections" document
        db.collection("connections")
            .document("slaveToMasterConnection")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle the error
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Connection status received from the slave
                    val isConnected = snapshot.getBoolean("connected") ?: false
                    updateConnectionStatus(isConnected)
                }
            }
    }

    private fun updateConnectionStatus(isConnected: Boolean) {
        // Update the UI based on the connection status
        val statusTextView: TextView = findViewById<TextView>(R.id.messageTextView)

        if (isConnected) {
            statusTextView.text = "Slave account has been connected"
            sendMessage("this a message from master to slave ")
        } else {
            statusTextView.text = "Waiting for the slave account to connect..."
        }
    }
    // Function to send a message
    private fun sendMessage(message: String) {
        val messageData = hashMapOf(
            "text" to message,
            "timestamp" to FieldValue.serverTimestamp(),
            "sender" to "master" // Add sender information if needed
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
                    val statusTextViewMaster = findViewById<TextView>(R.id.messageTextView)
                    statusTextViewMaster.text = buildString {
                        append(messageText)
                        append(" ")
                        append(sender)
                    }
                }
            }
    }

    companion object {
        public const val MY_REQUEST_CODE :Int = 2023
        public const val REQUEST_SMS_PERMISSIONS :Int = 2024
    }





}

*/