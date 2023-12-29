package com.smailgourmi.holdmycalls.data.db.entity

import com.google.firebase.database.PropertyName
import java.util.*

data class Call(
    @PropertyName("callerID") var callerID: String = "",
    @PropertyName("receiverID") var receiverID: String = "",
    @PropertyName("epochTimeMs") var epochTimeMs: Long = Date().time
)
