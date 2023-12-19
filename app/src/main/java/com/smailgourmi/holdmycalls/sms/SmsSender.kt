package com.smailgourmi.holdmycalls.sms

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import com.smailgourmi.holdmycalls.App
import com.smailgourmi.holdmycalls.data.Result
import com.smailgourmi.holdmycalls.data.db.entity.Message
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseReferenceChildObserver
import com.smailgourmi.holdmycalls.data.db.repository.DatabaseRepository
import com.smailgourmi.holdmycalls.util.convertTwoUserIDs


interface SmsSendCallback {
    fun onMessageSent(lastMessage: Message?)
    fun onMessageFailed(error: String)
    fun onMessageDelivered(lastMessage: Message?)
}

class SmsSender() : SmsSendCallback {
    private val dbRepository: DatabaseRepository = DatabaseRepository()
    private val fbRefMessagesChildObserver = FirebaseReferenceChildObserver()

    fun loadAndObserveNewMessages(context: Context) {
        dbRepository.loadAndObserveLastMessages(App.myUserID,fbRefMessagesChildObserver,
            fun(lastMessage: Result<Message>) {
                if (lastMessage is Result.Success && lastMessage.data !== null && lastMessage.data.senderID == App.myUserID && !lastMessage.data.seen) {
                    dbRepository.loadContact(App.myUserID, lastMessage.data.receiverID,
                        fun(contactResult: Result<UserContact>) {
                            if (contactResult is Result.Success) {
                                contactResult.data?.let(fun(contact: UserContact) {
                                    sendSMSMessage(context,contact.phoneNumber, lastMessage.data)
                                })
                            }
                        })

                }
            })
    }

    private fun sendSMSMessage(context: Context,receiverPhoneNumber: String,lastMessage: Message?) =
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

    override fun onMessageSent(lastMessage: Message?) {

    }

    override fun onMessageFailed(error: String) {

    }

    override fun onMessageDelivered(lastMessage: Message?) {
        if (lastMessage != null) {
            lastMessage.seen =true
            dbRepository.updateChatLastMessage(
                convertTwoUserIDs(
                    App.myUserID,
                    lastMessage.receiverID
                ),lastMessage)
        }
    }


}