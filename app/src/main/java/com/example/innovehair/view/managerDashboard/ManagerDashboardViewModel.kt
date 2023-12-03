package com.example.innovehair.view.managerDashboard

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.sharedPreferences.SharedPreferencesManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ManagerDashboardViewModel: ViewModel() {

    private val _data = MutableLiveData<String>()
    val data: LiveData<String>
        get() = _data

    private val _viewState = MutableLiveData<ManagerDashboardViewState>()
    val viewState: LiveData<ManagerDashboardViewState>
        get() = _viewState

    suspend fun getUserData(refreshLogin: Boolean, context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val sharedPreferencesManager = SharedPreferencesManager(context)
                val serviceFirebase = ServiceFirebase()
                val email = sharedPreferencesManager.getEmail() ?: ""
                val password = sharedPreferencesManager.getPassword() ?: ""

                if (refreshLogin) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnFailureListener {
                            FirebaseAuth.getInstance().signOut()
                        }.await()
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val name = serviceFirebase.getUser(userId).await().getString("name") ?: ""
                    _data.postValue(name)
                    _viewState.postValue(ManagerDashboardViewState.Success)
                } else {
                    val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val name = serviceFirebase.getUser(currentUser).await().getString("name") ?: ""
                    _data.postValue(name)
                    _viewState.postValue(ManagerDashboardViewState.Success)
                }
            } catch (e: Exception) {
                _viewState.postValue(ManagerDashboardViewState.Error)
            }
        }
    }
}

sealed class ManagerDashboardViewState {
    object Success : ManagerDashboardViewState()
    object Error : ManagerDashboardViewState()
    object Loading : ManagerDashboardViewState()
}