package com.smailgourmi.holdmycalls.ui.chats

import androidx.lifecycle.*
import com.smailgourmi.holdmycalls.data.model.ChatWithContactInfo
import com.smailgourmi.holdmycalls.ui.DefaultViewModel
import com.smailgourmi.holdmycalls.data.Event
import com.smailgourmi.holdmycalls.data.Result
import com.smailgourmi.holdmycalls.data.db.entity.Chat
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseReferenceValueObserver
import com.smailgourmi.holdmycalls.data.db.repository.DatabaseRepository
import com.smailgourmi.holdmycalls.util.addNewItem
import com.smailgourmi.holdmycalls.util.convertTwoUserIDs
import com.smailgourmi.holdmycalls.util.updateItemAt


class ChatsViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatsViewModel(myUserID) as T
    }
}

class ChatsViewModel(val myUserID: String) : DefaultViewModel() {

    private val repository: DatabaseRepository = DatabaseRepository()
    private val firebaseReferenceObserverList = ArrayList<FirebaseReferenceValueObserver>()
    private val _updatedChatWithContactInfo = MutableLiveData<ChatWithContactInfo>()
    private val _selectedChat = MutableLiveData<Event<ChatWithContactInfo>>()
    var selectedChat: LiveData<Event<ChatWithContactInfo>> = _selectedChat
    val chatsList = MediatorLiveData<MutableList<ChatWithContactInfo>>()

    init {
        chatsList.addSource(_updatedChatWithContactInfo) { newChat ->
            val chat = chatsList.value?.find { it.mChat.info.id == newChat.mChat.info.id }
            if (chat == null) {
                chatsList.addNewItem(newChat)
            } else {
                chatsList.updateItemAt(newChat, chatsList.value!!.indexOf(chat))
            }
        }
        setupChats()
    }

    override fun onCleared() {
        super.onCleared()
        firebaseReferenceObserverList.forEach { it.clear() }
    }

    private fun setupChats() {
        loadContacts()
    }

    private fun loadContacts() {
        repository.loadContacts(myUserID) { result: Result<List<UserContact>> ->
            onResult(null, result)
            if (result is Result.Success) result.data?.forEach { loadAndObserveChat(it) }
        }
    }


    private fun loadAndObserveChat(userContact: UserContact) {
        val observer = FirebaseReferenceValueObserver()
        firebaseReferenceObserverList.add(observer)
        repository.loadAndObserveChat(convertTwoUserIDs(myUserID, userContact.contactID), observer) { result: Result<Chat> ->
            if (result is Result.Success) {
                _updatedChatWithContactInfo.value = result.data?.let {
                    it.info.id = convertTwoUserIDs(myUserID,userContact.contactID)
                    ChatWithContactInfo(it, userContact) }
            } else if (result is Result.Error) {
                chatsList.value?.let {
                    val newList = mutableListOf<ChatWithContactInfo>().apply { addAll(it) }
                    newList.removeIf { it2 ->
                        result.msg.toString().contains(it2.mContactInfo.contactID)
                    }
                    chatsList.value = newList
                }
            }
        }
    }

    fun selectChatWithUserInfoPressed(chat: ChatWithContactInfo) {
        //chat.mChat.info.id = convertTwoUserIDs(myUserID,chat.mContactInfo.contactID)
        _selectedChat.value = Event(chat)
    }

    fun onResume() {
        loadContacts()
    }
}