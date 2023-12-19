@file:Suppress("unused")

package com.smailgourmi.holdmycalls.ui.chats

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smailgourmi.holdmycalls.R
import com.smailgourmi.holdmycalls.data.model.ChatWithContactInfo
import com.smailgourmi.holdmycalls.data.db.entity.Message


@BindingAdapter("bind_chats_list")
fun bindChatsList(listView: RecyclerView, items: List<ChatWithContactInfo>?) {
    items?.let { (listView.adapter as ChatsListAdapter).submitList(items) }
}

@BindingAdapter("bind_chat_message_text", "bind_chat_message_text_viewModel")
fun TextView.bindMessageYouToText(message: Message, viewModel: ChatsViewModel) {
    this.text = if (message.senderID == viewModel.myUserID) {
        "You: " + message.text
    } else {
        message.text
    }
}

@BindingAdapter("bind_message_view", "bind_message_textView", "bind_message", "bind_myUserID")
fun View.bindMessageSeen(view: View, textView: TextView, message: Message, myUserID: String) {
    if (message.senderID != myUserID && !message.seen) {
        view.visibility = View.VISIBLE
        textView.setTextAppearance(R.style.MessageNotSeen)
//        textView.alpha = 1f
    } else {
        view.visibility = View.INVISIBLE
        textView.setTextAppearance(R.style.MessageSeen)
//        textView.alpha = 1f
    }
}

