package com.smailgourmi.holdmycalls

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await


class SMSMessage{
    var sms:String?=null
    var phoneNumber:String?=null
    var senderUid:String?=null
    var timestamp :Long?=null

    constructor(){}
    constructor(sms: String?, phoneNumber:String?,senderUid:String?,timestamp:Long) {
        this.sms = sms
        this.phoneNumber = phoneNumber
        this.senderUid =senderUid
        this.timestamp = timestamp
    }
    public override operator fun equals(other: Any? ): Boolean{
        if( other is SMSMessage){
            val smsOther = (other as SMSMessage)
            return sms == smsOther.sms && timestamp == smsOther.timestamp && phoneNumber == smsOther.phoneNumber && senderUid == smsOther.senderUid
        }
        return  false
    }

    override fun hashCode(): Int {
        var result = sms?.hashCode() ?: 0
        result = 31 * result + (phoneNumber?.hashCode() ?: 0)
        result = 31 * result + (senderUid?.hashCode() ?: 0)
        result = 31 * result + (timestamp?.hashCode() ?: 0)
        return result
    }


}

class SMSMessagePagingSource(
    private val firebaseDatabase: DatabaseReference,
    private val contactPhoneNumber: String
) : PagingSource<Int, SMSMessage>() {

    private val userUid: String = FirebaseAuth.getInstance().currentUser!!.uid
    private val mDbRef: DatabaseReference = firebaseDatabase.child("chats").child(userUid)
        .child(contactPhoneNumber).child("messages")

    private var lastKeyNodeLoaded: String? = null
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SMSMessage> {
        try {
            // Implement logic to load chat messages based on params.key
            // Use mDbRef.child("chats").child(userUid).child(contactPhoneNumber) to get messages
            val currentPage = params.key ?: 0
            val startOffset = currentPage * params.loadSize
            val querySnapshot: DataSnapshot = if (lastKeyNodeLoaded == null) {
                mDbRef.orderByKey().limitToLast(params.loadSize).get().await()
            } else {
                mDbRef.orderByKey().endBefore(lastKeyNodeLoaded).limitToLast(params.loadSize).get().await()
            }

            val smsMessages = ArrayList<SMSMessage>()
            lastKeyNodeLoaded = querySnapshot.children.first().key
            if (querySnapshot != null) {
                for (data in querySnapshot.children) {
                    val smsMessage = data.getValue(SMSMessage::class.java)
                    if (smsMessage != null) {
                        smsMessages.add(smsMessage)
                    }
                }
            }
            return LoadResult.Page(
                data = smsMessages.reversed(),
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (smsMessages.size < params.loadSize) null else currentPage + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SMSMessage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

