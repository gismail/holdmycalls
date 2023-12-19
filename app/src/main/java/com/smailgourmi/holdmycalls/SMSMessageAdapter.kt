package com.smailgourmi.holdmycalls

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SMSMessageAdapter(var context: Context)
    : PagingDataAdapter<SMSMessage, SMSMessageAdapter.SMSViewHolder>(SMSComparator) {

    val ITEM_RECEIVE = 1
    val ITEM_SEND = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMSViewHolder {
        if(viewType == ITEM_RECEIVE){
            //inflate RECEIVE
            val view: View = LayoutInflater.from(context).inflate(R.layout.recieve_layout,parent,false)
            return ReceiveViewHolder(view)
        }else{
            //Inflate SEND
            val view: View = LayoutInflater.from(context).inflate(R.layout.send_layout,parent,false)
            return SendViewHolder(view)
        }
    }


    override fun onBindViewHolder(holder: SMSViewHolder, position: Int) {
        val  currentSMSMessage = getItem(position)
        if(holder.javaClass == SendViewHolder::class.java){
            val viewHolder = holder as SendViewHolder
            if (currentSMSMessage != null) {
                holder.sendSMSMessage.text = currentSMSMessage.sms
            }
            holder.smsTime.text =
                buildString {
                    if (currentSMSMessage != null) {
                        append(getFormattedTime(context, currentSMSMessage.timestamp!!))
                    }
                    append(" · SMS")
                }

        }else{
            //To Receive View Holder
            val viewHolder = holder as ReceiveViewHolder
            if (currentSMSMessage != null) {
                holder.receiveSMSMessage.text = currentSMSMessage.sms
            }
            holder.smsTime.text =
                buildString {
                    if (currentSMSMessage != null) {
                        append(getFormattedTime(context, currentSMSMessage.timestamp!!))
                    }
                    append(" · SMS")
                }
        }
    }

    override fun getItemViewType(position: Int): Int {

        val currentSMSMessage = getItem(position)

        if (currentSMSMessage != null) {
            return if(FirebaseAuth.getInstance().currentUser!!.uid == currentSMSMessage.senderUid){
                ITEM_SEND
            }else{
                ITEM_RECEIVE
            }
        }
        return 0
    }

    suspend fun insertNewMessage(message: SMSMessage) {

        val currentList : ArrayList<SMSMessage> = snapshot().items as ArrayList<SMSMessage>
        currentList.add(0,message)
        submitData(PagingData.from(currentList))
    }


        open class SMSViewHolder(itemView: View) : ViewHolder(itemView){}

        class SendViewHolder(itemView: View) : SMSViewHolder(itemView){
            val sendSMSMessage: TextView = itemView.findViewById<TextView>(R.id.txt_send_sms_message)
            val smsTime :TextView = itemView.findViewById<TextView>(R.id.sms_time)
            init {
                itemView.setOnClickListener {
                    smsTime.visibility = if (smsTime.visibility == GONE) VISIBLE else GONE
                }
            }
        }

        class ReceiveViewHolder(itemView: View) : SMSViewHolder(itemView){
            val receiveSMSMessage: TextView = itemView.findViewById<TextView>(R.id.txt_receive_sms_message)
            val smsTime :TextView = itemView.findViewById<TextView>(R.id.sms_time)
            init {
                itemView.setOnClickListener {
                    smsTime.visibility = if (smsTime.visibility == GONE) VISIBLE else GONE
                }
            }
        }


    companion object SMSComparator : DiffUtil.ItemCallback<SMSMessage>() {
        override fun areItemsTheSame(oldItem: SMSMessage, newItem: SMSMessage): Boolean {
            // Id is unique.
            return oldItem.senderUid == newItem.senderUid
        }

        override fun areContentsTheSame(oldItem: SMSMessage, newItem: SMSMessage): Boolean {
            return oldItem == newItem
        }
    }

}

