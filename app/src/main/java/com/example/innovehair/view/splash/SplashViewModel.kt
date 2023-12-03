package com.example.innovehair.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.utils.Event
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SplashViewModel : ViewModel() {

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean>
        get() = _isAdmin

    private val _error = MutableLiveData<Event<Unit>>()
    val error: LiveData<Event<Unit>>
        get() = _error

    fun checkUserSession() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        viewModelScope.launch {
            try {
                if (currentUser != null) {
                    val serviceFirebase = ServiceFirebase()
                    val userId = currentUser.uid

                    val userSnapshot = serviceFirebase.getUser(userId).await()

                    if (userSnapshot.exists()) {
                        val isAdmin = userSnapshot.getBoolean("isAdmin")
                        if (isAdmin != null) {
                            _isAdmin.value = isAdmin!!
                        } else {
                            FirebaseAuth.getInstance().signOut()
                            _error.value = Event(Unit)
                        }
                    } else {
                        // Usuário não encontrado, faça logout
                        FirebaseAuth.getInstance().signOut()
                        _error.value = Event(Unit)
                    }
                } else {
                    // Usuário não autenticado, faça logout
                    FirebaseAuth.getInstance().signOut()
                    _error.value = Event(Unit)
                }
            } catch (e: Exception) {
                // Trate qualquer exceção aqui
                FirebaseAuth.getInstance().signOut()
                _error.value = Event(Unit)
            }
        }
    }
}