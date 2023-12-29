package com.smailgourmi.holdmycalls.ui.calls

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.databinding.ListItemCallBinding


class CallsListAdapter internal constructor(private val viewModel: CallsViewModel) :
    ListAdapter<UserContact, CallsListAdapter.ViewHolder>(UserContactDiffCallback()) {

    class ViewHolder(private val binding: ListItemCallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: CallsViewModel, item: UserContact) {
            binding.viewmodel = viewModel
            binding.userContact = item
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemCallBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}

class UserContactDiffCallback : DiffUtil.ItemCallback<UserContact>() {
    override fun areItemsTheSame(oldItem: UserContact, newItem: UserContact): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserContact, newItem: UserContact): Boolean {
        return oldItem.contactID == newItem.contactID
    }
}