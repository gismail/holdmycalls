package com.smailgourmi.holdmycalls.sms

import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import com.smailgourmi.holdmycalls.App.Companion.myUserID
import com.smailgourmi.holdmycalls.data.Result
import com.smailgourmi.holdmycalls.data.db.entity.Message
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseReferenceChildObserver
import com.smailgourmi.holdmycalls.data.db.repository.DatabaseRepository
import com.smailgourmi.holdmycalls.util.convertTwoUserIDs
import com.smailgourmi.holdmycalls.util.hashPhoneNumber


interface SmsSendCallback {
    fun onMessageSent(lastMessage: Message?)
    fun onMessageFailed(error: String)
    fun onMessageDelivered(lastMessage: Message?)
}


class SmsSender() : SmsSendCallback{
    private val dbRepository: DatabaseRepository = DatabaseRepository()
    private val fbRefMessagesChildObserver = FirebaseReferenceChildObserver()

    fun loadAndObserveNewMessages(
        context: Context
    ) {
        dbRepository.loadAndObserveLastMessages(
            myUserID,
            fbRefMessagesChildObserver
        ) { result: Result<Message> ->
            if(result is Result.Success){
                if(result.data !== null) {
                    if (result.data.senderID == myUserID && !result.data.seen) {
                        dbRepository.loadContact(myUserID,result.data.receiverID){
                            if(it is Result.Success){

                                it.data?.let { it1 ->
                                    sendSMSMessage(context,
                                        it1.phoneNumber,result.data)
                                }

                            }
                        }

                    }
                }

            }

        }
    }

    private fun sendSMSMessage(
        context: Context,
        receiverPhoneNumber: String,
        lastMessage: Message?
    ) {
        // on the below line we are creating a try and catch block
        try {

            val deliveryIntent = Intent("SMS_DELIVERED_ACTION")
            val deliveredPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                deliveryIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val sentPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent("SMS_SENT_ACTION"),
                PendingIntent.FLAG_IMMUTABLE
            )

            // Register a BroadcastReceiver to handle the delivery status
            context.registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent?.action == "SMS_DELIVERED_ACTION") {
                            // SMS delivered successfully
                            onMessageDelivered(lastMessage)
                        }
                    }
                },
                IntentFilter("SMS_DELIVERED_ACTION"), Context.RECEIVER_NOT_EXPORTED
            )

            // Register a BroadcastReceiver to handle the send status
            context.registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent?.action == "SMS_SENT_ACTION") {
                            val resultCode = resultCode

                            when (resultCode) {
                                Activity.RESULT_OK -> {
                                    // SMS sent successfully
                                    onMessageSent(lastMessage)
                                }
                                SmsManager.RESULT_ERROR_GENERIC_FAILURE,
                                SmsManager.RESULT_ERROR_NO_SERVICE,
                                SmsManager.RESULT_ERROR_NULL_PDU,
                                SmsManager.RESULT_ERROR_RADIO_OFF -> {
                                    // SMS sending failed
                                    onMessageFailed("Error: SMS sending failed")
                                }
                            }
                        }
                    }
                },
                IntentFilter("SMS_SENT_ACTION"), Context.RECEIVER_NOT_EXPORTED
            )


            // on below line we are initializing sms manager.
            //as after android 10 the getDefault function no longer works
            //so we have to check that if our android version is greater
            //than or equal toandroid version 6.0 i.e SDK 23
            //if SDK is greater that or equal to 23 then
            //this is how we will initialize the SmsManager
            val smsManager: SmsManager = context.getSystemService(SmsManager::class.java)
            // on below line we are sending text message.
            smsManager.sendTextMessage(
                receiverPhoneNumber,
                null,
                lastMessage!!.text,
                sentPendingIntent,
                deliveredPendingIntent
            )

        } catch (e: Exception) {

            // on catch block we are displaying toast message for error.
            onMessageFailed("Error: ${e.message}")
        }

    }

    override fun onMessageSent(lastMessage: Message?) {

    }

    override fun onMessageFailed(error: String) {

    }

    override fun onMessageDelivered(lastMessage: Message?) {
        if (lastMessage != null) {
            lastMessage.seen =true
            dbRepository.updateChatLastMessage(convertTwoUserIDs(myUserID,lastMessage.receiverID),lastMessage)
        }
    }


}


class SmsReceiver : BroadcastReceiver() {
    private val TAG = this::class.java.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle: Bundle? = intent.extras
            val messages: Array<SmsMessage?>
            val format = bundle?.getString("format")

            // Retrieve the SMS message received
            messages = if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<*>?
                messagesFromPdus(pdus, format)
            } else {
                emptyArray()
            }

            // Process each SMS message
            for (message in messages) {
                val sender = message?.originatingAddress
                val messageBody = message?.messageBody

                // Handle the SMS message as needed
                Log.d(TAG, "Sender: $sender, Message: $messageBody")
                // Process the SMS message and display a notification if needed
                processReceivedSMS(message)

            }
        }
    }



    private fun processReceivedSMS(message: SmsMessage?) {
        if (message != null) {
            if (!message.messageBody.isNullOrBlank()) {
                val contactID = hashPhoneNumber(message.originatingAddress!!)
                val chatID = convertTwoUserIDs(myUserID, contactID)
                val newMsg = Message(contactID, myUserID, message.messageBody).apply {
                    epochTimeMs = message.timestampMillis
                }
                val dbRepository = DatabaseRepository()
                updateNewReceivedMessage(dbRepository,chatID,newMsg)
                updateChatLastMessage(dbRepository,chatID,newMsg)
                updateContactsList(dbRepository,contactID, message.originatingAddress!!)

            }
        }
    }

    private fun updateChatLastMessage(
        dbRepository: DatabaseRepository,
        chatID: String,
        newMsg: Message
    ) {
        dbRepository.updateNewMessage(chatID, newMsg)
    }

    private fun updateNewReceivedMessage(
        dbRepository: DatabaseRepository,
        chatID: String,
        newMsg: Message
    ) {
        dbRepository.updateChatLastMessage(chatID, newMsg)
    }
    private fun updateContactsList(
        dbRepository: DatabaseRepository,
        contactID: String,
        originatingAddress: String
    ) {
        dbRepository.loadContact(myUserID,contactID) { result: Result<UserContact> ->
            if (result is Result.Success && result.data == null) {
                dbRepository.addContact(
                    myUserID,
                    UserContact(originatingAddress, originatingAddress)
                )
            }
        }
    }
    private fun messagesFromPdus(pdus: Array<*>?, format: String?): Array<SmsMessage?> {
        return pdus?.map { pdu ->
            SmsMessage.createFromPdu(pdu as ByteArray, format)
        }?.toTypedArray() ?: emptyArray()
    }
}

class SmsReceiverService : Service() {

    private val binder = SmsReceiverBinder()
    private val smsReceiver: BroadcastReceiver = SmsReceiver()
    private val smsSender:SmsSender = SmsSender()
    override fun onCreate() {
        super.onCreate()

        // Register the SmsReceiver dynamically
        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsReceiver, intentFilter)

        // Start observing new messages
        smsSender.loadAndObserveNewMessages(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the SmsReceiver
        unregisterReceiver(smsReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private inner class SmsReceiverBinder : Binder() {
        fun getService(): SmsReceiverService {
            return this@SmsReceiverService
        }
    }

}

