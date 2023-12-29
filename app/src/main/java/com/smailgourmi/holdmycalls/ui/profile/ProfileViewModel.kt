package com.smailgourmi.holdmycalls.ui.profile

import android.net.Uri
import androidx.lifecycle.*
import com.smailgourmi.holdmycalls.App
import com.smailgourmi.holdmycalls.data.Event
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseReferenceValueObserver
import com.smailgourmi.holdmycalls.data.db.repository.DatabaseRepository
import com.smailgourmi.holdmycalls.ui.DefaultViewModel
import com.smailgourmi.holdmycalls.data.Result
import com.smailgourmi.holdmycalls.data.db.entity.Chat
import com.smailgourmi.holdmycalls.data.db.entity.ChatInfo
import com.smailgourmi.holdmycalls.data.db.entity.Message
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.data.db.repository.StorageRepository
import com.smailgourmi.holdmycalls.data.model.ChatWithContactInfo
import com.smailgourmi.holdmycalls.util.convertTwoUserIDs


class ProfileViewModelFactory(private val myUserID: String, private val otherUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(myUserID, otherUserID) as T
    }
}

enum class LayoutState {
    IS_FRIEND, NOT_FRIEND, ACCEPT_DECLINE
}

class ProfileViewModel(private val myUserID: String, private val contactID: String) :
    DefaultViewModel() {

    private val repository: DatabaseRepository = DatabaseRepository()
    private val storageRepository = StorageRepository()
    private val firebaseReferenceObserver = FirebaseReferenceValueObserver()

    private val _editDisplayNameEvent = MutableLiveData<Event<Unit>>()
    val editDisplayNameEvent: LiveData<Event<Unit>> = _editDisplayNameEvent

    private val _editStatusEvent = MutableLiveData<Event<Unit>>()
    val editStatusEvent: LiveData<Event<Unit>> = _editStatusEvent

    private val _editImageEvent = MutableLiveData<Event<Unit>>()
    val editImageEvent: LiveData<Event<Unit>> = _editImageEvent

    private val _selectedSendSMS = MutableLiveData<Event<ChatWithContactInfo>>()
    var selectedSendSMS: LiveData<Event<ChatWithContactInfo>> = _selectedSendSMS

    private val _selectedCall = MutableLiveData<Event<Unit>>()
    var selectedCall: LiveData<Event<Unit>> = _selectedCall

    private val _userContact: MutableLiveData<UserContact> = MutableLiveData()
    val userContact: LiveData<UserContact> = _userContact


    init {
        setupProfile()
    }

    override fun onCleared() {
        super.onCleared()
        firebaseReferenceObserver.clear()
    }

    private fun setupProfile() {
        repository.loadContact(myUserID,contactID) { result: Result<UserContact> ->
            onResult(_userContact, result)
            if (result is Result.Success) {
                repository.loadAndObserveContact(myUserID,contactID, firebaseReferenceObserver) { result2: Result<UserContact> ->
                    onResult(_userContact, result2)
                }
            }
        }
    }


    fun changeDisplayNamePressed() {
        _editDisplayNameEvent.value = Event(Unit)
    }

    fun changeStatusPressed(){
        _editStatusEvent.value = Event(Unit)
    }
    fun changeImageURLPressed(){
        _editImageEvent.value = Event(Unit)
    }
    fun makeCallPressed(){
        _selectedCall.value = Event(Unit)
    }



    fun sendSMSPressed(){
        val mChatInfo = ChatInfo(convertTwoUserIDs(App.myUserID,contactID))
        val mChat = Chat(Message(),mChatInfo)
        val chatWithContactInfo = ChatWithContactInfo(mChat,_userContact.value!!)
        _selectedSendSMS.value = Event(chatWithContactInfo)
    }

    fun changeDisplayName(displayName: String) {
        repository.updateContactDisplayNAme(myUserID,contactID, displayName)
    }

    fun changeStatus(status: String) {
        repository.updateContactStatus(myUserID,contactID,status)
    }
    fun changeContactImage(byteArray: ByteArray) {
        storageRepository.updateContactProfileImage(myUserID,contactID, byteArray) { result: Result<Uri> ->
            onResult(null, result)
            if (result is Result.Success) {
                repository.updateContactProfileImageUrl(myUserID,contactID, result.data.toString())
            }
        }
    }
}
