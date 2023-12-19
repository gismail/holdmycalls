package com.smailgourmi.holdmycalls.pagination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.database.DatabaseReference
import com.smailgourmi.holdmycalls.SMSMessagePagingSource


class MainViewModel(
    private val firebaseDatabase: DatabaseReference,
    private val contactPhoneNumber: String
): ViewModel() {

    val data = Pager(
        PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
    ) {
        SMSMessagePagingSource(firebaseDatabase,contactPhoneNumber)
    }.flow.cachedIn(viewModelScope)

}

class MainViewModelFactory(
    private val firebaseDatabase: DatabaseReference,
    private val contactPhoneNumber: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(firebaseDatabase,contactPhoneNumber) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
