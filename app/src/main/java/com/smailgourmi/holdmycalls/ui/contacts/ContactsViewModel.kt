package com.smailgourmi.holdmycalls.ui.contacts


import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smailgourmi.holdmycalls.data.Event
import com.smailgourmi.holdmycalls.data.Result
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseReferenceChildObserver
import com.smailgourmi.holdmycalls.data.db.repository.DatabaseRepository
import com.smailgourmi.holdmycalls.ui.DefaultViewModel


class UsersViewModelFactory(private val myUserID: String, val fragment: Fragment) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactsViewModel(myUserID,fragment) as T
    }
}

class ContactsViewModel(private val myUserID: String, fragment: Fragment) : DefaultViewModel() {
    private val repository: DatabaseRepository = DatabaseRepository()
    private val _selectedContact = MutableLiveData<Event<UserContact>>()
    var selectedContact: LiveData<Event<UserContact>> = _selectedContact
    private val updatedUsersList = MutableLiveData<List<UserContact>>()
    val contactsList = MediatorLiveData<List<UserContact>>()
    private val firebaseReferenceObserver = FirebaseReferenceChildObserver()
    init {
        contactsList.addSource(updatedUsersList) { mutableList ->
            contactsList.value = updatedUsersList.value
        }
        loadAndObserveContactsInfo()
        loadContacts()
    }

    fun loadContacts() {
        repository.loadContacts(myUserID) { result: Result<List<UserContact>> ->
            onResult(updatedUsersList, result)
        }



    }
    private fun loadAndObserveContactsInfo() {
        repository.loadAndObserveContacts(myUserID,firebaseReferenceObserver){
                result:Result<UserContact> -> updateContactsList(result)
        }
    }

    private fun updateContactsList(result: Result<UserContact>) {
        val newList : ArrayList<UserContact>? = updatedUsersList.value?.let { ArrayList(it) }
        when(result){
            is Result.Success ->{
                if (newList != null) {
                    result.data?.let { addOrUpdateContact(newList,it )}
                    updatedUsersList.value = newList!!
                }
            }

            else -> {}
        }

    }
    fun addOrUpdateContact(contactsList: MutableList<UserContact>, userContact: UserContact) {
            val existingUser = contactsList.find { it.contactID == userContact.contactID }

            if (existingUser != null) {
                // If the contactID already exists, update the existing user
                existingUser.apply {
                    displayName = userContact.displayName
                    phoneNumber = userContact.phoneNumber
                    profileImageUrl = userContact.profileImageUrl
                    status = userContact.status
                }
            } else {
                // If the contactID doesn't exist, add the new user to the list
                contactsList.add(userContact)
            }
        }

    fun selectContact(contact: UserContact) {
        _selectedContact.value = Event(contact)
    }

}


