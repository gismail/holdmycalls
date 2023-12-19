package com.smailgourmi.holdmycalls


import android.content.Context
import android.text.format.DateFormat
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.smailgourmi.holdmycalls.data.db.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar

fun fromFirebase(data: DataSnapshot): SMSMessage {
    val senderId = data.child("senderUid").getValue(String::class.java)
    val phoneNumber = data.child("phoneNumber").getValue(String::class.java)
    val content = data.child("sms").getValue(String::class.java)
    val timestamp = data.child("timestamp").getValue(Long::class.java)
    return SMSMessage(content,phoneNumber,senderId, timestamp!!)
}

suspend fun  getCurrentUser(mDbRef: DatabaseReference, auth: FirebaseAuth): User? {
    val currentUser = mDbRef.child("users").child(auth.currentUser!!.uid)
        .get().await()

    if (currentUser.exists()) {
       val email = currentUser.child("email").getValue(String::class.java)
       val name = currentUser.child("name").getValue(String::class.java)
       val phoneNumber = currentUser.child("phoneNumber").getValue(String::class.java)
       val uid = currentUser.child("uid").getValue(String::class.java)
       return null
    }
    return null
}


fun getFormattedTime(context: Context, timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.setTimeInMillis(timestamp)

    val formattedTime = if (DateFormat.is24HourFormat(context)) {
        val dateFormat = SimpleDateFormat("HH:mm")
        dateFormat.format(calendar.timeInMillis)
    } else {
        val dateFormat = SimpleDateFormat("HH:mm 'PM'")
        dateFormat.format(calendar.timeInMillis)
    }

    return formattedTime
}


