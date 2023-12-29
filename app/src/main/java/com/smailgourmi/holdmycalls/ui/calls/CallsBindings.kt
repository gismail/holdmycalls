package com.smailgourmi.holdmycalls.ui.calls

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smailgourmi.holdmycalls.data.db.entity.UserContact


@BindingAdapter("bind_calls_list")
fun bindCallsList(listView: RecyclerView, items: List<UserContact>?) {
    items?.let { (listView.adapter as CallsListAdapter).submitList(items) }
}
