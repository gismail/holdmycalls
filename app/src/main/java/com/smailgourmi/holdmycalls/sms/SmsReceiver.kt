package com.smailgourmi.holdmycalls.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import com.smailgourmi.holdmycalls.App
import com.smailgourmi.holdmycalls.data.Result
import com.smailgourmi.holdmycalls.data.db.entity.Message
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.data.db.repository.DatabaseRepository
import com.smailgourmi.holdmycalls.util.convertTwoUserIDs
import com.smailgourmi.holdmycalls.util.hashPhoneNumber

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
                val chatID = convertTwoUserIDs(App.myUserID, contactID)
                val newMsg = Message(contactID, App.myUserID, message.messageBody).apply {
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
        dbRepository.updateNewMessage(App.myUserID,chatID, newMsg)
    }

    private fun updateNewReceivedMessage(
        dbRepository: DatabaseRepository,
        chatID: String,
        newMsg: Message
    ) {
        dbRepository.updateChatLastMessage(App.myUserID,chatID, newMsg)
    }
    private fun updateContactsList(
        dbRepository: DatabaseRepository,
        contactID: String,
        originatingAddress: String
    ) {
        dbRepository.loadContact(App.myUserID,contactID) { result: Result<UserContact> ->
            if (result is Result.Success && result.data == null) {
                dbRepository.addContact(
                    App.myUserID,
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