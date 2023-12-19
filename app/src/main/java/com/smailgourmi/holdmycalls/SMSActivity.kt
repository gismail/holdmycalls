package com.smailgourmi.holdmycalls


import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.smailgourmi.holdmycalls.pagination.MainLoadStateAdapter
import com.smailgourmi.holdmycalls.pagination.MainViewModel
import com.smailgourmi.holdmycalls.pagination.MainViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class SMSActivity() : AppCompatActivity() {
    private  lateinit var smsMessageRecyclerView: RecyclerView
    private lateinit var smsMessageBox :EditText
    private lateinit var sendMessage : ImageView
    private lateinit var smsMessageAdapter: SMSMessageAdapter
    private lateinit var msgList: ArrayList<SMSMessage>
    private lateinit var mDbRef: DatabaseReference
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(mDbRef,intent.getStringExtra("phoneNumber")!!) }
    var receiverRoom: String?=null
    var senderRoom: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)

        val name = intent.getStringExtra("name")
        val contactPhoneNumber = intent.getStringExtra("phoneNumber")
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance(getString(R.string.database_reference)).reference

        senderRoom = contactPhoneNumber + userUid
        receiverRoom = userUid + contactPhoneNumber

        supportActionBar?.title = name

        smsMessageBox = findViewById(R.id.sms_message_box)
        smsMessageRecyclerView = findViewById(R.id.sms_recycler_view_activity)
        sendMessage = findViewById(R.id.sms_send_message)

        msgList = ArrayList()
        smsMessageAdapter = SMSMessageAdapter(this)

        smsMessageRecyclerView.layoutManager = LinearLayoutManager(this)
        smsMessageRecyclerView.adapter = smsMessageAdapter.withLoadStateHeaderAndFooter(
            header = MainLoadStateAdapter (),
            footer = MainLoadStateAdapter ()
        )

        (smsMessageRecyclerView.layoutManager as LinearLayoutManager).reverseLayout = true
        smsMessageRecyclerView.scrollToPosition(0)

        lifecycleScope.launch {
            viewModel.data.collectLatest {
                smsMessageAdapter.submitData(it)
            }
        }

        smsMessageAdapter.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.NotLoading &&
                loadState.append.endOfPaginationReached &&
                smsMessageAdapter.itemCount < 10 // Change this threshold as needed
            ) {
                // Load more data when user scrolls to the bottom and not all data is loaded
                smsMessageAdapter.refresh()
            }
        }

        smsMessageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItemPosition == totalItemCount - 1) {
                    // Reached the end of the list
                    // No need to manually trigger refresh here
                }
            }
        })

        /*
        var loadParams = LoadParams<Int,SMSMessage>(0, 5, 0)
        CoroutineScope(Dispatchers.Main).launch {
            // Launch the first coroutine
            val job = async {
                SMSMessageLoader(mDbRef, Contact(name, contactPhoneNumber),
                    getCurrentUser(mDbRef, FirebaseAuth.getInstance())!!
                )
            }

            // Wait for the result of the first coroutine
            smsMessageLoader = job.await()
            mDbRef.child("chats").child(userUid!!).child(contactPhoneNumber!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    CoroutineScope(Dispatchers.Main).launch {
                        //Logic for adding data to recycler view
                        msgList.clear()
                        val loadResult : LoadResult<Int, SMSMessage> = smsMessageLoader.load(loadParams)
                        val page = (loadResult as LoadResult.Page<Int,SMSMessage>)
                        loadParams.previousKey = page.prevKey
                        loadParams.key=page.nextKey
                        msgList.addAll(0,loadResult.data.reversed())

                    }
                    smsMessageAdapter.notifyItemChanged(0)
                    smsMessageRecyclerView.scrollToPosition(0)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }*/
        //Add message to database
        sendMessage.setOnClickListener{
            val message = smsMessageBox.text.toString()
            if(message.isNotEmpty()) {
                val timestamp = Date().time
                val messageObject =
                    SMSMessage(
                        message, contactPhoneNumber,
                        FirebaseAuth.getInstance().currentUser!!.uid, timestamp
                    )

                if (contactPhoneNumber != null && userUid != null) {
                    mDbRef.child(getString(R.string.chats)).child(userUid).child(contactPhoneNumber)
                        .child(getString(R.string.messages))
                        .push().setValue(messageObject).addOnSuccessListener {
                            mDbRef.child(getString(R.string.chats)).child(userUid)
                                .child(getString(R.string.metadata))
                                .setValue(MetaData(contactPhoneNumber, timestamp))
                            smsMessageBox.text.clear()
                            // Refresh the UI after sending the message
                            lifecycleScope.launch {
                                smsMessageAdapter.insertNewMessage(messageObject)
                            }

                        }
                }
            }

        }
    }
}

class MetaData  {
    var timestamp :Long?=null
    var phoneNumber:String?=null

    constructor(){}
    constructor(phoneNumber:String?,timestamp: Long?) {
        this.phoneNumber = phoneNumber
        this.timestamp = timestamp
    }
}