package com.smailgourmi.holdmycalls.util

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.smailgourmi.holdmycalls.ui.chats.ChatsFragment
import com.smailgourmi.holdmycalls.ui.main.MainActivity





fun requestPermissions(fragment: ChatsFragment, permissions: Array<String>, requestCode: Int){
    // Check if permission is granted
    if (permissions.any {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }) {
        ActivityCompat.requestPermissions(
            (fragment.activity as MainActivity),
            permissions,
            requestCode
        )
    }
}
