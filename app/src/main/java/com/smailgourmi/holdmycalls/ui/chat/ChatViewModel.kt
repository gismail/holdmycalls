package com.smailgourmi.holdmycalls.ui.chat

import androidx.lifecycle.*
import com.smailgourmi.holdmycalls.data.db.entity.Chat
import com.smailgourmi.holdmycalls.data.db.entity.Message
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseReferenceChildObserver
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseReferenceValueObserver
import com.smailgourmi.holdmycalls.data.db.repository.DatabaseRepository
import com.smailgourmi.holdmycalls.data.Result
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.ui.DefaultViewModel
import com.smailgourmi.holdmycalls.util.addNewItem


class ChatViewModelFactory(private val myUserID: String, private val contactID: String, private val chatID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(myUserID, contactID, chatID) as T
    }
}

class ChatViewModel(private val myUserID: String, private val contactID: String, private val chatID: String) : DefaultViewModel() {

    private val dbRepository: DatabaseRepository = DatabaseRepository()

    private val _userContact: MutableLiveData<UserContact> = MutableLiveData()
    private val _addedMessage = MutableLiveData<Message>()

    private val fbRefMessagesChildObserver = FirebaseReferenceChildObserver()
    private val fbRefUserInfoObserver = FirebaseReferenceValueObserver()

    //private val smsSender : SmsSender = SmsSender()

    val messagesList = MediatorLiveData<MutableList<Message>>()
    val newMessageText = MutableLiveData<String?>()
    val userContact: LiveData<UserContact> = _userContact

    init {
        setupChat()
        checkAndUpdateLastMessageSeen()
        //smsSender.loadAndObserveNewMessages(dbRepository,fbRefMessagesChildObserver,App.application.applicationContext)

    }

    override fun onCleared() {
        super.onCleared()
        fbRefMessagesChildObserver.clear()
        fbRefUserInfoObserver.clear()
    }

    private fun checkAndUpdateLastMessageSeen() {
        dbRepository.loadChat(myUserID, chatID) { result: Result<Chat> ->
            if (result is Result.Success && result.data != null) {
                result.data.lastMessage.let {
                    if (!it.seen && it.senderID != myUserID) {
                        it.seen = true
                        dbRepository.updateChatLastMessage(myUserID,chatID, it)
                    }
                }
            }
        }
    }

    private fun setupChat() {
        dbRepository.loadAndObserveContact(myUserID,contactID, fbRefUserInfoObserver) { result: Result<UserContact> ->
            onResult(_userContact, result)
            if (result is Result.Success && !fbRefMessagesChildObserver.isObserving()) {
                loadAndObserveNewMessages()
            }
        }
    }

    private fun loadAndObserveNewMessages() {
        messagesList.addSource(_addedMessage) { messagesList.addNewItem(it) }

        dbRepository.loadAndObserveMessagesAdded(myUserID,
            chatID,
            fbRefMessagesChildObserver
        ) { result: Result<Message> ->
            onResult(_addedMessage, result)
        }
    }

    fun sendMessagePressed() {
        if (!newMessageText.value.isNullOrBlank()) {
            val newMsg = Message(myUserID,contactID, newMessageText.value!!)
            dbRepository.updateNewMessage(myUserID,chatID, newMsg)
            dbRepository.updateChatLastMessage(myUserID,chatID, newMsg)
            newMessageText.value = null
        }
    }
}