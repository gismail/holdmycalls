package com.smailgourmi.holdmycalls.ui.contacts

import android.content.ContentUris
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.smailgourmi.holdmycalls.App
import com.smailgourmi.holdmycalls.data.db.entity.UserContact
import com.smailgourmi.holdmycalls.data.db.repository.DatabaseRepository
import com.smailgourmi.holdmycalls.util.formatPhoneNumber


class ContactPicker(private val fragment: Fragment){
    var contactsPicker: ActivityResultLauncher<Void?>
    private val repository: DatabaseRepository = DatabaseRepository()

    init {
    contactsPicker = fragment.registerForActivityResult(ActivityResultContracts.PickContact()) { contactUri ->
        handleContactPicked(contactUri)

    }}

    fun openContactsPicker() {
        contactsPicker.launch(null)
    }
    private fun handleContactPicked(contactUri: android.net.Uri?) {
        contactUri?.let { uri ->
            val projection: Array<String> = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            val cursor = fragment.requireActivity().contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf(ContentUris.parseId(uri).toString()),
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val phoneNumberColumnIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val contactPhoneNumber = cursor.getString(phoneNumberColumnIndex)
                    // Now you have the contact name and phone number
                    val contactName = getContactDisplayName(uri)
                    val contact = UserContact(contactName, formatPhoneNumber(contactPhoneNumber))
                    repository.addContact(App.myUserID, contact)
                    // Handle the selected contact as needed
                    // For example, update UI or perform other actions
                }
            }
        }
    }
    private fun getContactDisplayName(contactUri: android.net.Uri): String {
        val projection: Array<String> = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val cursor = fragment.requireActivity().contentResolver.query(
            contactUri,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val nameColumnIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                return it.getString(nameColumnIndex)
            }
        }

        return ""
    }
}