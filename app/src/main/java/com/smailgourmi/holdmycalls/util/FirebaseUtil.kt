package com.smailgourmi.holdmycalls.util

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.getValue

fun <T> wrapSnapshotToClass(className: Class<T>, snap: DataSnapshot): T? {
    return snap.getValue(className)
}
fun <T> wrapSnapshotToClassChild(className: Class<T>, snap: DataSnapshot,path:String?): T? {
    return if(path !== null){
        snap.child(path).getValue(className)
    }else{
        snap.getValue(className)
    }

}

fun <T> wrapSnapshotToArrayList(className: Class<T>, snap: DataSnapshot): MutableList<T> {
    val arrayList: MutableList<T> = arrayListOf()
    for (child in snap.children) {
        child.getValue(className)?.let { arrayList.add(it) }
    }
    return arrayList
}

// Always returns the same combined id when comparing the two users id's
fun convertTwoUserIDs(userID1: String, userID2: String): String {
    return if (userID1 < userID2) {
        userID2 + userID1
    } else {
        userID1 + userID2
    }
}