package com.smailgourmi.holdmycalls.ui.call

import android.telecom.Connection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smailgourmi.holdmycalls.data.Event
import com.smailgourmi.holdmycalls.data.Result
import com.smailgourmi.holdmycalls.data.db.entity.Chat
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseReferenceChildObserver
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseReferenceValueObserver
import com.smailgourmi.holdmycalls.data.db.repository.DatabaseRepository
import com.smailgourmi.holdmycalls.ui.DefaultViewModel


class CallViewModelFactory(private val myUserID: String, private val contactID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CallViewModel(myUserID, contactID) as T
    }
}

class CallViewModel(private val myUserID: String, private val contactID: String) : DefaultViewModel() {

    private val dbRepository: DatabaseRepository = DatabaseRepository()

    private val _selectedCall = MutableLiveData<Event<Unit>>()
    var selectedCall: LiveData<Event<Unit>> = _selectedCall

    private val _userContact: MutableLiveData<UserContact> = MutableLiveData()
    val userContact: LiveData<UserContact> = _userContact
    // Add this property to the class
    val outgoingConnection: LiveData<Connection> = MutableLiveData()

    init {
        setupContact()
    }

    private fun setupContact() {
        dbRepository.loadContact(myUserID,contactID) { result: Result<UserContact> ->
            onResult(_userContact, result)
            if (result is Result.Success) {
                onResult(_userContact, result)
                _selectedCall.value = Event(Unit)
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
    }



}