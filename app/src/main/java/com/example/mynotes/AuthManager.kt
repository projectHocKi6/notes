package com.example.mynotes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AuthManager {
    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(isUserLoggedIn())
    val authState: StateFlow<Boolean> = _authState

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    init {
        auth.addAuthStateListener {
            _authState.value = isUserLoggedIn()
        }
    }
}
