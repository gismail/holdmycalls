package com.smailgourmi.holdmycalls.ui.start.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.smailgourmi.holdmycalls.data.Event
import com.smailgourmi.holdmycalls.data.Result
import com.smailgourmi.holdmycalls.data.db.repository.AuthRepository
import com.smailgourmi.holdmycalls.data.model.Login
import com.smailgourmi.holdmycalls.ui.DefaultViewModel
import com.smailgourmi.holdmycalls.util.isEmailValid
import com.smailgourmi.holdmycalls.util.isTextValid

class LoginViewModel : DefaultViewModel() {

    val authRepository = AuthRepository()
    private val _isLoggedInEvent = MutableLiveData<Event<FirebaseUser>>()
    private val _forgotClicked = MutableLiveData<Event<Unit>>()
    private val _signupClicked = MutableLiveData<Event<Unit>>()



    val isLoggedInEvent: LiveData<Event<FirebaseUser>> = _isLoggedInEvent
    val emailText = MutableLiveData<String>() // Two way
    val passwordText = MutableLiveData<String>() // Two way
    val isLoggingIn = MutableLiveData<Boolean>() // Two way
    val forgotClicked : MutableLiveData<Event<Unit>> = _forgotClicked
    val signUpClicked : MutableLiveData<Event<Unit>> = _signupClicked
    private fun login() {
        isLoggingIn.value = true
        val login = Login(emailText.value!!, passwordText.value!!)

        authRepository.loginUser(login) { result: Result<FirebaseUser> ->
            onResult(null, result)
            if (result is Result.Success) _isLoggedInEvent.value = Event(result.data!!)
            if (result is Result.Success || result is Result.Error) isLoggingIn.value = false
        }
    }

    fun loginPressed() {
        if (!isEmailValid(emailText.value.toString())) {
            mSnackBarText.value = Event("Invalid email format")
            return
        }
        if (!isTextValid(6, passwordText.value)) {
            mSnackBarText.value = Event("Password is too short")
            return
        }

        login()
    }

    fun fogotPWDPressed(){
        _forgotClicked.value = Event(Unit)
    }

    fun signUpPressed(){
        _signupClicked.value = Event(Unit)
    }
}