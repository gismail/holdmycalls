package com.smailgourmi.holdmycalls.data.db.repository

import com.smailgourmi.holdmycalls.data.db.remote.FirebaseAuthSource
import com.smailgourmi.holdmycalls.data.model.CreateUser
import com.smailgourmi.holdmycalls.data.model.Login
import com.smailgourmi.holdmycalls.data.Result
import com.google.firebase.auth.FirebaseUser
import com.smailgourmi.holdmycalls.data.db.remote.FirebaseAuthStateObserver

class AuthRepository{
    private val firebaseAuthService = FirebaseAuthSource()

    fun observeAuthState(stateObserver: FirebaseAuthStateObserver, b: ((Result<FirebaseUser>) -> Unit)){
        firebaseAuthService.attachAuthStateObserver(stateObserver,b)

    }

    fun loginUser(login: Login, b: ((Result<FirebaseUser>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseAuthService.loginWithEmailAndPassword(login).addOnSuccessListener {
            b.invoke(Result.Success(it.user))
        }.addOnFailureListener {
            b.invoke(Result.Error(msg = it.message))
        }
    }

    fun createUser(createUser: CreateUser, b: ((Result<FirebaseUser>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseAuthService.createUser(createUser).addOnSuccessListener {
            b.invoke(Result.Success(it.user))
        }.addOnFailureListener {
            b.invoke(Result.Error(msg = it.message))
        }
    }

    fun logoutUser() {
        firebaseAuthService.logout()
    }

    fun resetPasswordwithEmail(userEmail: String,b: ((Result<String>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseAuthService.resetPasswordWithEmail(userEmail).addOnSuccessListener{
            b.invoke(Result.Success("Check your email"))
        }.addOnFailureListener{
            b.invoke(Result.Error(it.message))
        }
    }
}