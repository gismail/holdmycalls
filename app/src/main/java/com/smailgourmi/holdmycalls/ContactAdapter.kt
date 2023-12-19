package com.smailgourmi.holdmycalls

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES.S
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smailgourmi.holdmycalls.data.db.entity.User

class ContactAdapter(val context: Context, val contactList :ArrayList<User>) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.contact_layout,parent,false)
        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentContact = contactList[position]

    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val contactName: TextView = itemView.findViewById<TextView>(R.id.textUserName)
        val smsButton: ImageView = itemView.findViewById<ImageView>(R.id.imageSms)
    }
}


