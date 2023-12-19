package com.smailgourmi.holdmycalls.data.model

import com.smailgourmi.holdmycalls.data.db.entity.Chat
import com.smailgourmi.holdmycalls.data.db.entity.UserContact

data class ChatWithContactInfo(
    var mChat: Chat,
    var mContactInfo: UserContact
)
