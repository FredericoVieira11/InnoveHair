package com.example.innovehair.view.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.respondeModels.UserResponse
import com.example.innovehair.sharedPreferences.SharedPreferencesManager
import com.example.innovehair.utils.Event
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    private val _dataFromFirestore = MutableLiveData<UserResponse>()
    val dataFromFirestore: LiveData<UserResponse>
        get() = _dataFromFirestore

    private val _error = MutableLiveData<Event<Unit>>()
    val error: LiveData<Event<Unit>>
        get() = _error

    private val _viewState = MutableLiveData<LoginViewState>()
    val viewState: LiveData<LoginViewState>
        get() = _viewState

    fun signInUser(context: Context, email: String, password: String) {
        viewModelScope.launch {
            _viewState.value = LoginViewState.Loading
            try {
                val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid ?: ""
                val documentSnapshot = ServiceFirebase().getUser(userId).await()
                if (documentSnapshot.exists()) {
                    val isAdmin = documentSnapshot.getBoolean("isAdmin")
                    if (isAdmin == null) {
                        _viewState.value = LoginViewState.Error
                        return@launch
                    }
                    val result = UserResponse(
                        id = documentSnapshot.reference.id,
                        name = documentSnapshot.getString("name").toString(),
                        isAdmin = isAdmin
                    )
                    SharedPreferencesManager(context).saveCredentials(email, password)
                    Log.d("SharedPreferences", "Credenciais salvas: $email, $password")
                    _dataFromFirestore.value = result
                    _viewState.value = LoginViewState.Success
                } else {
                    _viewState.value = LoginViewState.Error
                }
            } catch (e: Exception) {
                _viewState.value = LoginViewState.Error
            }
        }
    }
}

sealed class LoginViewState {
    object Success : LoginViewState()
    object Error : LoginViewState()
    object Loading : LoginViewState()
}