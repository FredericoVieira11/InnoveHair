package com.example.innovehair.view.resetPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResetPasswordViewModel : ViewModel() {

    private val _viewState = MutableLiveData<ResetPasswordViewState>()
    val viewState: LiveData<ResetPasswordViewState>
        get() = _viewState

    fun sendResetPassword(email: String) {
        viewModelScope.launch {
            _viewState.value = ResetPasswordViewState.Loading
            val serviceFirebase = ServiceFirebase()
            val emailExists = withContext(Dispatchers.IO) {
                serviceFirebase.checkEmailExists(email)
            }

            if (emailExists) {
                val resetPasswordSuccess = withContext(Dispatchers.IO) {
                    serviceFirebase.resetPassword(email)
                }
                _viewState.value = if (resetPasswordSuccess) {
                    ResetPasswordViewState.Success
                } else {
                    ResetPasswordViewState.Error
                }
            } else {
                withContext(Dispatchers.IO) {
                    serviceFirebase.resetPassword(email)
                }
                _viewState.value = ResetPasswordViewState.Error
            }
        }
    }
}

sealed class ResetPasswordViewState {
    object Success : ResetPasswordViewState()
    object Error : ResetPasswordViewState()
    object Loading : ResetPasswordViewState()
}