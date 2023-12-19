package com.smailgourmi.holdmycalls.ui.contacts

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smailgourmi.holdmycalls.data.db.entity.UserContact

@BindingAdapter("bind_users_list")
fun bindContactsList(listView: RecyclerView, items: List<UserContact>?) {
    items?.let { (listView.adapter as ContactsListAdapter).submitList(items) }
}

