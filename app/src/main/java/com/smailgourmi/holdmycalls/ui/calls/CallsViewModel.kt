package com.smailgourmi.holdmycalls.ui.calls

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smailgourmi.holdmycalls.data.Result
import com.smailgourmi.holdmycalls.data.db.entity.Call
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.data.db.repository.DatabaseRepository
import com.smailgourmi.holdmycalls.ui.DefaultViewModel
import com.smailgourmi.holdmycalls.util.addNewItem


class CallsViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CallsViewModel(myUserID) as T
    }
}

class CallsViewModel(private val myUserID: String) : DefaultViewModel() {

    private val dbRepository: DatabaseRepository = DatabaseRepository()
    private val updatedUserContactInfo = MutableLiveData<UserContact>()
    private val userCallsList = MutableLiveData<MutableList<Call>>()

    val userContactsList = MediatorLiveData<MutableList<UserContact>>()

    init {
        userContactsList.addSource(updatedUserContactInfo) { userContactsList.addNewItem(it) }
        loadCalls()
    }

    private fun loadCalls() {
        dbRepository.loadCalls(myUserID) { result: Result<MutableList<Call>> ->
            onResult(userCallsList, result)
            if (result is Result.Success) result.data?.forEach {
                if (it.callerID== myUserID) {
                    loadContactInfo(it.receiverID)
                } else {
                    loadContactInfo(it.callerID)
                }
            }
        }
    }

    private fun loadContactInfo(contactID: String) {
        dbRepository.loadContact(myUserID,contactID) { result: Result<UserContact> ->
            onResult(updatedUserContactInfo, result)
        }
    }

    /*private fun updateNotification(otherUserInfo: UserContact, removeOnly: Boolean) {
        val userNotification = userContactsList.value?.find {
            it.contactID == otherUserInfo.contactID
        }

        if (userNotification != null) {
            if (!removeOnly) {
                dbRepository.updateNewFriend(UserContact(myUserID), UserContact(otherUserInfo.id))
                val newChat = Chat().apply {
                    info.id = convertTwoUserIDs(myUserID, otherUserInfo.id)
                    lastMessage = Message(seen = true, text = "Say hello!")
                }
                dbRepository.updateNewChat(myUserID,newChat)
            }
            dbRepository.removeNotification(myUserID, otherUserInfo.id)
            dbRepository.removeSentRequest(otherUserInfo.id, myUserID)

            usersInfoList.removeItem(otherUserInfo)
            userContactsList.removeItem(userNotification)
        }
    }*/

    fun makeCallPressed(userContact: UserContact) {
        //updateNotification(userContact, false)
    }

}