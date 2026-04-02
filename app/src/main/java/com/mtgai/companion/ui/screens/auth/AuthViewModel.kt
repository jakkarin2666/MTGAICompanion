package com.mtgai.companion.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        _currentUser.value = auth.currentUser
        _isLoggedIn.value = auth.currentUser != null
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                checkCurrentUser()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String, displayName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    // Save user to Firestore
                    val userDoc = mapOf(
                        "uid" to user.uid,
                        "email" to email,
                        "displayName" to displayName,
                        "createdAt" to System.currentTimeMillis()
                    )
                    db.collection("users").document(user.uid).set(userDoc).await()
                    checkCurrentUser()
                    onSuccess()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginAsGuest(onSuccess: () -> Unit) {
        // For guest mode, we just skip auth
        _isLoggedIn.value = true
        onSuccess()
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun clearError() {
        _error.value = null
    }
}
