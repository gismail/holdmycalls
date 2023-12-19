package com.smailgourmi.holdmycalls.sms

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder


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

