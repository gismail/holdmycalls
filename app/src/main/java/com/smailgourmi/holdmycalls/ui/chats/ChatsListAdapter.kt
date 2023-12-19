package com.smailgourmi.holdmycalls.ui.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smailgourmi.holdmycalls.data.model.ChatWithContactInfo
import com.smailgourmi.holdmycalls.databinding.ListItemChatBinding

class ChatsListAdapter internal constructor(private val viewModel: ChatsViewModel) :
    ListAdapter<(ChatWithContactInfo), ChatsListAdapter.ViewHolder>(ChatDiffCallback()) {

    class ViewHolder(private val binding: ListItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: ChatsViewModel, item: ChatWithContactInfo) {
            binding.viewmodel = viewModel
            binding.chatwithuserinfo = item
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemChatBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<ChatWithContactInfo>() {
    override fun areItemsTheSame(oldItem: ChatWithContactInfo, itemWithUserInfo: ChatWithContactInfo): Boolean {
        return oldItem == itemWithUserInfo
    }

    override fun areContentsTheSame(oldItem: ChatWithContactInfo, itemWithUserInfo: ChatWithContactInfo): Boolean {
        return oldItem.mChat.info.id == itemWithUserInfo.mChat.info.id
    }
}