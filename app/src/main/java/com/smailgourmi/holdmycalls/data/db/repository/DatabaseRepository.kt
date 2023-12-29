package com.smailgourmi.holdmycalls.data.db.repository

import com.smailgourmi.holdmycalls.util.wrapSnapshotToArrayList
import com.smailgourmi.holdmycalls.util.wrapSnapshotToClass
import com.smailgourmi.holdmycalls.data.db.entity.*
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseDataSource
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseReferenceChildObserver
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseReferenceValueObserver
import com.smailgourmi.holdmycalls.data.db.entity.Chat
import com.smailgourmi.holdmycalls.data.db.entity.Message
import com.smailgourmi.holdmycalls.data.db.entity.User
import com.smailgourmi.holdmycalls.data.db.entity.UserInfo
import com.smailgourmi.holdmycalls.data.Result

class DatabaseRepository {
    private val firebaseDatabaseService = FirebaseDataSource()

    // region update Contact
    fun updateContactStatus(userID: String, contactID:String,status: String) {
        firebaseDatabaseService.updateContactStatus(userID,contactID, status)
    }
    fun updateContactDisplayNAme(userID: String, contactID:String,displayName: String) {
        firebaseDatabaseService.updateContactDisplayName(userID,contactID, displayName)
    }


    //region Update User
    fun updateUserStatus(userID: String, status: String) {
        firebaseDatabaseService.updateUserStatus(userID, status)
    }

    fun updateNewMessage(userID: String,messagesID: String, message: Message) {
        firebaseDatabaseService.pushNewMessage(userID, messagesID, message)
    }

    fun updateMessage(userID: String,messagesID: String,epochTimeMs: Double){
        firebaseDatabaseService.updateMessage(userID, messagesID, epochTimeMs)
    }

    fun updateNewUser(user: User) {
        firebaseDatabaseService.updateNewUser(user)
    }

    fun updateNewFriend(myUser: UserContact, otherUser: UserContact) {
        firebaseDatabaseService.updateNewFriend(myUser, otherUser)
    }


    /*fun updateNewNotification(otherUserID: String, userNotification: UserNotification) {
        firebaseDatabaseService.updateNewNotification(otherUserID, userNotification)
    }*/

    fun updateChatLastMessage(userID: String,chatID: String, message: Message) {
        firebaseDatabaseService.updateLastMessage(userID,chatID, message,)
    }

    fun updateNewChat(userID: String,chat: Chat){
        firebaseDatabaseService.updateNewChat(userID,chat)
    }

    fun updateUserProfileImageUrl(userID: String, url: String){
        firebaseDatabaseService.updateUserProfileImageUrl(userID, url)
    }
    fun updateContactProfileImageUrl(userID: String,contactID: String, url: String){
        firebaseDatabaseService.updateContactProfileImageUrl(userID, contactID , url)
    }

    //endregion

    //region Remove
    fun removeNotification(userID: String, notificationID: String) {
        firebaseDatabaseService.removeNotification(userID, notificationID)
    }

    fun removeFriend(userID: String, friendID: String) {
        firebaseDatabaseService.removeFriend(userID, friendID)
    }
    fun addContact(userID: String, userContact: UserContact) {
        firebaseDatabaseService.addContact(userID, userContact)
    }

    fun removeSentRequest(otherUserID: String, myUserID: String) {
        firebaseDatabaseService.removeSentRequest(otherUserID, myUserID)
    }

    fun removeChat(userID: String,chatID: String) {
        firebaseDatabaseService.removeChat(userID, chatID)
    }

    fun removeMessages(userID: String,messagesID: String){
        firebaseDatabaseService.removeMessages(userID, messagesID)
    }

    //endregion

    //region Load Single

    fun loadUser(userID: String, b: ((Result<User>) -> Unit)) {
        firebaseDatabaseService.loadUserTask(userID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(User::class.java, it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }
    fun loadContact(userID: String,contactID: String, b: ((Result<UserContact>) -> Unit)) {
        firebaseDatabaseService.loadContactTask(userID,contactID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(UserContact::class.java, it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadUserInfo(userID: String, b: ((Result<UserInfo>) -> Unit)) {
        firebaseDatabaseService.loadUserInfoTask(userID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(UserInfo::class.java, it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadChat(userID: String, chatID: String, b: (Result<Chat>) -> Unit) {
        firebaseDatabaseService.loadChatTask(userID, chatID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(Chat::class.java, it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadMessagesAdded(myUserID: String, messagesIDs: String, b:(Result<MutableList<Message>>) -> Unit) {
        firebaseDatabaseService.loadMessages(myUserID,messagesIDs).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToArrayList(Message::class.java,it)))
        }.addOnFailureListener{b.invoke(Result.Error(it.message))}
    }

    //endregion

    //region Load List

    fun loadUsers(b: ((Result<MutableList<User>>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadUsersTask().addOnSuccessListener {
            val usersList = wrapSnapshotToArrayList(User::class.java, it)
            b.invoke(Result.Success(usersList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadContacts(userID: String, b: (Result<List<UserContact>>) -> Unit) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadConatacsTask(userID).addOnSuccessListener {
            val friendsList = wrapSnapshotToArrayList(UserContact::class.java, it)
            b.invoke(Result.Success(friendsList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadCalls(userID: String, b: ((Result<MutableList<Call>>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadCallsTask(userID).addOnSuccessListener {
            val callsList = wrapSnapshotToArrayList(Call::class.java, it)
            b.invoke(Result.Success(callsList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    //endregion

    //#region Load and Observe
    fun loadAndObserveUser(userID: String, observer: FirebaseReferenceValueObserver, b: ((Result<User>) -> Unit)) {
        firebaseDatabaseService.attachUserObserver(User::class.java, userID, observer, b)
    }
    fun loadAndObserveContact(userID: String, contactID: String, observer: FirebaseReferenceValueObserver, b: ((Result<UserContact>) -> Unit)) {
        firebaseDatabaseService.attachContactObserver(UserContact::class.java, userID, contactID , observer, b)
    }
    fun loadAndObserveContacts(userID: String, observer: FirebaseReferenceChildObserver, b: (Result<UserContact>) -> Unit) {
        firebaseDatabaseService.attachContactsObserver(UserContact::class.java, userID, observer, b)
    }
    fun loadAndObserveLastMessages(userID: String, observer: FirebaseReferenceChildObserver, b: (Result<Message>) -> Unit) {
        firebaseDatabaseService.attachLastMessagesObserver(Message::class.java, userID, observer, b)
    }

    fun loadAndObserveUserInfo(userID: String, observer: FirebaseReferenceValueObserver, b: (Result<UserInfo>) -> Unit) {
        firebaseDatabaseService.attachUserInfoObserver(UserInfo::class.java, userID, observer, b)
    }

    fun loadAndObserveUserCalls(userID: String, observer: FirebaseReferenceValueObserver, b: ((Result<MutableList<Call>>) -> Unit)){
        firebaseDatabaseService.attachUserCallsObserver(Call::class.java, userID, observer, b)
    }

    fun loadAndObserveMessagesAdded(userID: String,messagesID: String, observer: FirebaseReferenceChildObserver, b: ((Result<Message>) -> Unit)) {
        firebaseDatabaseService.attachMessagesObserver(
            Message::class.java,userID,
            messagesID,
            observer,
            b
        )
    }

    fun loadAndObserveChildrenMessagesAdded(userID: String,observer: FirebaseReferenceChildObserver,b: ((Result<MutableList<Message>>) -> Unit)){
        firebaseDatabaseService.attachChildrenMessagesObserver(Message::class.java,userID,observer,b)

    }

    fun loadAndObserveChat(userID: String,chatID: String, observer: FirebaseReferenceValueObserver, b: ((Result<Chat>) -> Unit)) {
        firebaseDatabaseService.attachChatObserver(Chat::class.java,userID, chatID, observer, b)
    }



    //endregion
}

