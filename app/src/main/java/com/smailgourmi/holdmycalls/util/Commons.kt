package com.smailgourmi.holdmycalls.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.smailgourmi.holdmycalls.ui.main.MainActivity





fun requestPermissions(context: Context, permissions: Array<String>, requestCode: Int){
    // Check if permission is granted
    if (permissions.any {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }) {
        ActivityCompat.requestPermissions(
            (context as MainActivity),
            permissions,
            requestCode
        )
    }
}
