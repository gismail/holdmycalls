package com.smailgourmi.holdmycalls.ui.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.smailgourmi.holdmycalls.App
import com.smailgourmi.holdmycalls.R
import com.smailgourmi.holdmycalls.data.EventObserver
import com.smailgourmi.holdmycalls.data.model.ChatWithContactInfo
import com.smailgourmi.holdmycalls.databinding.FragmentChatsBinding

import com.smailgourmi.holdmycalls.ui.chat.ChatFragment
import com.smailgourmi.holdmycalls.util.convertTwoUserIDs


class ChatsFragment : Fragment() {

    private val viewModel: ChatsViewModel by viewModels { ChatsViewModelFactory(App.myUserID) }
    private lateinit var viewDataBinding: FragmentChatsBinding
    private lateinit var listAdapter: ChatsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
            FragmentChatsBinding.inflate(inflater, container, false).apply {
                viewmodel = viewModel
                lifecycleOwner = viewLifecycleOwner
            }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAdapter()
        setupObservers()
    }

    private fun setupListAdapter() {
        listAdapter = ChatsListAdapter(viewModel)
        viewDataBinding.chatsRecyclerView.adapter = listAdapter

    }

    private fun setupObservers() {
        viewModel.selectedChat.observe(viewLifecycleOwner,
            EventObserver { navigateToChat(it) })
    }

    private fun navigateToChat(chatWithContactInfo: ChatWithContactInfo) {
        val bundle = bundleOf(
            ChatFragment.ARGS_KEY_USER_ID to App.myUserID,
            ChatFragment.ARGS_KEY_USER_CONTACT_ID to chatWithContactInfo.mContactInfo.contactID,
            ChatFragment.ARGS_KEY_CHAT_ID to chatWithContactInfo.mChat.info.id
        )
        findNavController().navigate(R.id.action_navigation_chats_to_chatFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }
}