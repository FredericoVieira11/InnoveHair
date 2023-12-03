package com.example.innovehair.view.mainDashboard

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainDashboardViewModel : ViewModel() {

    private val _data = MutableLiveData<Pair<String, Uri?>>()
    val data: LiveData<Pair<String, Uri?>>
        get() = _data

    private val serviceFirebase = ServiceFirebase()

    init {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val userSnapshot = serviceFirebase.getUser(userId).await()
                    val name = userSnapshot.getString("name") ?: ""
                    val imageUri = try {
                        serviceFirebase.downloadImage(userId).await()
                    } catch (e: Exception) {
                        null
                    }
                    _data.value = Pair(name, imageUri)
                }
            } catch (e: Exception) {
                Log.e("Failed to get user data", "${e.message}")
            }
        }
    }
}

sealed class MainDashboardViewState {
    object Success : MainDashboardViewState()
    object Error : MainDashboardViewState()
    object Loading : MainDashboardViewState()
}