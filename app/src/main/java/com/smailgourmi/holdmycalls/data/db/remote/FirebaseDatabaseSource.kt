package com.smailgourmi.holdmycalls.data.db.remote

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.*
import com.smailgourmi.holdmycalls.data.Result
import com.smailgourmi.holdmycalls.data.db.entity.*
import com.smailgourmi.holdmycalls.util.wrapSnapshotToArrayList
import com.smailgourmi.holdmycalls.util.wrapSnapshotToClass
import com.smailgourmi.holdmycalls.util.wrapSnapshotToClassChild

class FirebaseReferenceConnectedObserver {

    private var valueEventListener: ValueEventListener? = null
    private var dbRef: DatabaseReference? = null
    private var userRef: DatabaseReference? = null

    fun start(userID: String) {
        this.userRef = FirebaseDataSource.dbInstance.reference.child("users/$userID/info/online")
        this.valueEventListener = getEventListener(userID)
        this.dbRef = FirebaseDataSource.dbInstance.getReference(".info/connected").apply { addValueEventListener(valueEventListener!!) }
    }

    private fun getEventListener(userID: String): ValueEventListener {
        return (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    FirebaseDataSource.dbInstance.reference.child("users/$userID/info/online").setValue(true)
                    userRef?.onDisconnect()?.setValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun clear() {
        valueEventListener?.let { dbRef?.removeEventListener(it) }
        userRef?.setValue(false)
        valueEventListener = null
        dbRef = null
        userRef = null
    }
}

class FirebaseReferenceValueObserver {
    private var valueEventListener: ValueEventListener? = null
    private var dbRef: DatabaseReference? = null

    fun start(valueEventListener: ValueEventListener, reference: DatabaseReference) {
        reference.addValueEventListener(valueEventListener)
        this.valueEventListener = valueEventListener
        this.dbRef = reference
    }

    fun clear() {
        valueEventListener?.let { dbRef?.removeEventListener(it) }
        valueEventListener = null
        dbRef = null
    }
}

class FirebaseReferenceChildObserver {
    private var valueEventListener: ChildEventListener? = null
    private var dbRef: DatabaseReference? = null
    private var isObserving: Boolean = false

    fun start(valueEventListener: ChildEventListener, reference: DatabaseReference) {
        isObserving = true
        reference.addChildEventListener(valueEventListener)
        this.valueEventListener = valueEventListener
        this.dbRef = reference
    }

    fun clear() {
        valueEventListener?.let { dbRef?.removeEventListener(it) }
        isObserving = false
        valueEventListener = null
        dbRef = null
    }

    fun isObserving(): Boolean {
        return isObserving
    }
}

// Task based
class FirebaseDataSource {

    companion object {
        val dbInstance = FirebaseDatabase.getInstance("https://hold-my-calls-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    //region Private

    private fun refToPath(path: String): DatabaseReference {
        return dbInstance.reference.child(path)
    }

    private fun attachValueListenerToTaskCompletion(src: TaskCompletionSource<DataSnapshot>): ValueEventListener {
        return (object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) { src.setException(Exception(error.message)) }

            override fun onDataChange(snapshot: DataSnapshot) { src.setResult(snapshot) }
        })
    }

    private fun <T> attachValueListenerToBlock(resultClassName: Class<T>, b: ((Result<T>) -> Unit)): ValueEventListener {
        return (object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) { b.invoke(Result.Error(error.message)) }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (wrapSnapshotToClass(resultClassName, snapshot) == null) {
                    b.invoke(Result.Error(msg = snapshot.key))
                } else {
                    b.invoke(Result.Success(wrapSnapshotToClass(resultClassName, snapshot)))
                }
            }
        })
    }

    private fun <T> attachValueListenerToBlockWithList(resultClassName: Class<T>, b: ((Result<MutableList<T>>) -> Unit)): ValueEventListener {
        return (object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) { b.invoke(Result.Error(error.message)) }
            override fun onDataChange(snapshot: DataSnapshot) {
                b.invoke(Result.Success(wrapSnapshotToArrayList(resultClassName, snapshot)))
            }
        })
    }

    private fun <T> attachChildListenerToBlock(resultClassName: Class<T>, b: ((Result<T>) -> Unit)): ChildEventListener {
        return (object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                b.invoke(Result.Success(wrapSnapshotToClass(resultClassName, snapshot)))
            }

            override fun onCancelled(error: DatabaseError) { b.invoke(Result.Error(error.message)) }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                b.invoke(Result.Success(wrapSnapshotToClass(resultClassName, snapshot)))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }
    private fun <T> attachChildrenListenerToBlock(path: String?,resultClassName: Class<T>, b: ((Result<T>) -> Unit)): ChildEventListener {
        return (object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                b.invoke(Result.Success(wrapSnapshotToClassChild(resultClassName, snapshot,path)))
            }
            override fun onCancelled(error: DatabaseError) { b.invoke(Result.Error(error.message)) }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                b.invoke(Result.Success(wrapSnapshotToClassChild(resultClassName, snapshot,path)))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }

    private fun <T> attachChildrenListenerToBlockWithList(resultClassName: Class<T>, b: ((Result<MutableList<T>>) -> Unit)): ChildEventListener {
        return (object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                b.invoke(Result.Success(wrapSnapshotToArrayList(resultClassName, snapshot)))
            }

            override fun onCancelled(error: DatabaseError) { b.invoke(Result.Error(error.message)) }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                b.invoke(Result.Success(wrapSnapshotToArrayList(resultClassName, snapshot)))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }

    //endregion

    //region Update Contact
    fun updateContactStatus(userID: String,contactID: String, status: String) {
        refToPath("users/$userID/contacts/$contactID/status").setValue(status)
    }
    fun updateContactDisplayName(userID: String,contactID: String, displayName: String) {
        refToPath("users/$userID/contacts/$contactID/displayName").setValue(displayName)
    }
    //region update User

    fun updateUserProfileImageUrl(userID: String, url: String ) {
        refToPath("users/$userID/info/profileImageUrl").setValue(url)
    }
    fun updateContactProfileImageUrl(userID: String,contactID: String, url: String ) {
        refToPath("users/$userID/contacts/$contactID/profileImageUrl").setValue(url)
    }


    fun updateUserStatus(userID: String, status: String) {
        refToPath("users/$userID/info/status").setValue(status)
    }

    fun updateLastMessage( userID: String,chatID: String, message: Message,) {
        refToPath("chats/$userID/$chatID/lastMessage").setValue(message)
    }

    fun updateNewFriend(myUser: UserContact, otherUser: UserContact) {
        refToPath("users/${myUser.contactID}/friends/${otherUser.contactID}").setValue(otherUser)
        //refToPath("users/${otherUser.contactID}/friends/${myUser.contactID}").setValue(myUser)
    }

    /*fun updateNewCall(otherUserID: String, userNotification: UserNotification) {
        refToPath("users/${otherUserID}/notifications/${userNotification.userID}").setValue(userNotification)
    }*/

    fun updateNewUser(user: User) {
        refToPath("users/${user.info.id}").setValue(user)
    }

    fun updateNewChat(userID: String,chat: Chat ) {
        refToPath("chats/$userID/${chat.info.id}").setValue(chat)
    }

    fun pushNewMessage(userID: String, messagesID: String, message: Message) {
        refToPath("messages/$userID/$messagesID").push().setValue(message)
    }

    fun updateMessage(userID: String, messagesID: String, epochTimeMs: Double){
        val query = refToPath("messages/$userID/$messagesID").orderByChild("epochTimeMs").equalTo(epochTimeMs)
        query.get().addOnSuccessListener {dataSnapShot : DataSnapshot ->
            dataSnapShot.children.forEach {
                it.ref.child("seen").setValue(true);
            }
        }
    }

    //endregion

    //region Remove

    fun removeNotification(userID: String, notificationID: String) {
        refToPath("users/${userID}/notifications/$notificationID").setValue(null)
    }

    fun removeFriend(userID: String, friendID: String) {
        refToPath("users/${userID}/friends/$friendID").setValue(null)
        refToPath("users/${friendID}/friends/$userID").setValue(null)
    }
    fun addContact(userID: String, userContact: UserContact) {
        refToPath("users/${userID}/contacts/${userContact.contactID}").setValue(userContact)
    }

    fun removeSentRequest(userID: String, sentRequestID: String) {
        refToPath("users/${userID}/sentRequests/$sentRequestID").setValue(null)
    }

    fun removeChat(userID: String, chatID: String) {
        refToPath("chats/${userID}/$chatID").setValue(null)
    }

    fun removeMessages(userID: String, messagesID: String) {
        refToPath("messages/${userID}/$messagesID").setValue(null)
    }

    //endregion

    //region Load
    fun loadUserTask(userID: String): Task<DataSnapshot> {
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskCompletion(src)
        refToPath("users/$userID").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadContactTask(userID: String,contactID: String): Task<DataSnapshot> {
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskCompletion(src)
        refToPath("users/$userID/contacts/$contactID").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadUserInfoTask(userID: String): Task<DataSnapshot> {
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskCompletion(src)
        refToPath("users/$userID/info").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadUsersTask(): Task<DataSnapshot> {
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskCompletion(src)
        refToPath("users").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadConatacsTask(userID: String): Task<DataSnapshot> {
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskCompletion(src)
        refToPath("users/$userID/contacts").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadChatTask(userID: String, chatID: String): Task<DataSnapshot> {
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskCompletion(src)
        refToPath("chats/${userID}/$chatID").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadMessages(myUserID: String, messagesIDs: String): Task<DataSnapshot> {
        return refToPath("messages/${myUserID}/${messagesIDs}")
            .orderByChild("seen").equalTo(false).get()

    }

    fun loadCallsTask(userID: String): Task<DataSnapshot> {
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskCompletion(src)
        refToPath("calls/$userID/calls").addListenerForSingleValueEvent(listener)
        return src.task
    }

    //endregion

    //region Value Observers

    fun <T> attachUserObserver(resultClassName: Class<T>, userID: String, refObs: FirebaseReferenceValueObserver, b: ((Result<T>) -> Unit)) {
        val listener = attachValueListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("users/$userID"))
    }
    fun <T> attachContactObserver(resultClassName: Class<T>, userID: String,contactID: String,refObs: FirebaseReferenceValueObserver, b: ((Result<T>) -> Unit)) {
        val listener = attachValueListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("users/$userID/contacts/$contactID"))
    }

    fun <T> attachUserInfoObserver(resultClassName: Class<T>, userID: String, refObs: FirebaseReferenceValueObserver, b: ((Result<T>) -> Unit)) {
        val listener = attachValueListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("users/$userID/info"))
    }

    fun <T> attachUserCallsObserver(resultClassName: Class<T>, userID: String, firebaseReferenceValueObserver: FirebaseReferenceValueObserver,
                                    b: ((Result<MutableList<T>>) -> Unit)
    ) {
        val listener = attachValueListenerToBlockWithList(resultClassName, b)
        firebaseReferenceValueObserver.start(listener, refToPath("calls/$userID/calls"))
    }

    fun <T> attachMessagesObserver(
        resultClassName: Class<T>,
        userID: String,
        messagesID: String,
        refObs: FirebaseReferenceChildObserver,
        b: (Result<T>) -> Unit
    ) {
        val listener = attachChildListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("messages/${userID}/$messagesID"))
    }

    fun <T> attachChildrenMessagesObserver(
        resultClassName: Class<T>,
        userID: String,
        refObs: FirebaseReferenceChildObserver,
        b: (Result<MutableList<T>>) -> Unit
    ) {
        val listener = attachChildrenListenerToBlockWithList(resultClassName, b)
        refObs.start(listener, refToPath("messages/${userID}/"))
    }

    fun <T> attachContactsObserver(resultClassName: Class<T>, userID: String, refObs: FirebaseReferenceChildObserver, b: (Result<T>) -> Unit) {
        val listener = attachChildListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("users/$userID/contacts"))
    }

    fun <T> attachLastMessagesObserver(resultClassName: Class<T>, userID: String, refObs: FirebaseReferenceChildObserver, b: (Result<T>) -> Unit) {
        val listener = attachChildrenListenerToBlock("lastMessage",resultClassName, b)
        refObs.start(listener, refToPath("chats/${userID}"))
    }

    fun <T> attachChatObserver(
        resultClassName: Class<T>,
        userID: String,
        chatID: String,
        refObs: FirebaseReferenceValueObserver,
        b: (Result<T>) -> Unit
    ) {
        val listener = attachValueListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("chats/${userID}/$chatID"))
    }



    //endregion
}
