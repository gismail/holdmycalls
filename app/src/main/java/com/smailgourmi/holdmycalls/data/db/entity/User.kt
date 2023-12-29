package com.smailgourmi.holdmycalls.data.db.entity

import com.google.firebase.database.PropertyName
import com.smailgourmi.holdmycalls.util.hashPhoneNumber


data class User(
    @get:PropertyName("info") @set:PropertyName("info") var info: UserInfo = UserInfo(),
    @get:PropertyName("contacts") @set:PropertyName("contacts") var contacts: HashMap<String, UserContact> = HashMap(),
)

data class UserContact(
    @get:PropertyName("displayName") @set:PropertyName("displayName") var displayName: String = "",
    @get:PropertyName("phoneNumber") @set:PropertyName("phoneNumber") var phoneNumber: String = "",
    @get:PropertyName("profileImageUrl") @set:PropertyName("profileImageUrl") var profileImageUrl: String = "",
    @get:PropertyName("status") @set:PropertyName("status") var status: String = "No status",
    @get:PropertyName("contactID") var contactID: String = hashPhoneNumber(phoneNumber)

)

data class UserInfo(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("displayName") @set:PropertyName("displayName") var displayName: String = "",
    @get:PropertyName("status") @set:PropertyName("status") var status: String = "No status",
    @get:PropertyName("profileImageUrl") @set:PropertyName("profileImageUrl") var profileImageUrl: String = "",
    @get:PropertyName("online") @set:PropertyName("online") var online: Boolean = false
)



