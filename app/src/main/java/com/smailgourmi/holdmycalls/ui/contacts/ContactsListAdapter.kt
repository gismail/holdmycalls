package com.smailgourmi.holdmycalls.ui.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.databinding.ListItemUserBinding


class ContactsListAdapter internal constructor(private val viewModel: ContactsViewModel) :
    ListAdapter<UserContact, ContactsListAdapter.ViewHolder>(ContactDiffCallback()) {

    class ViewHolder(private val binding: ListItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: ContactsViewModel, item: UserContact) {
            binding.viewmodel = viewModel
            binding.usercontact = item
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemUserBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}

class ContactDiffCallback : DiffUtil.ItemCallback<UserContact>() {
    override fun areItemsTheSame(oldItem: UserContact, newItem: UserContact): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserContact, newItem: UserContact): Boolean {
        return oldItem.contactID == newItem.contactID
    }
}