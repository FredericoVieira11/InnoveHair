package com.example.innovehair.view.editCollaborator

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditCollaboratorViewModel : ViewModel() {

    private val _data = MutableLiveData<CollaboratorData>()
    val data: LiveData<CollaboratorData>
        get() = _data

    private val _viewState = MutableLiveData<EditCollaboratorViewState>()
    val viewState: LiveData<EditCollaboratorViewState>
        get() = _viewState

    private val serviceFirebase = ServiceFirebase()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            try {
                val userDocument = serviceFirebase.getUser(userId).await()
                val user = userDocument.toObject(User::class.java)

                val profileImageUri: Uri? = try {
                    serviceFirebase.downloadImage(userId).await()
                } catch (e: Exception) {
                    null
                }

                if (user != null) {
                    val collaboratorData = CollaboratorData(userId, user)
                    withContext(Dispatchers.Main) {
                        _data.value = collaboratorData.copy(uri = profileImageUri)
                    }
                }
            } catch (e: Exception) {
                _viewState.postValue(EditCollaboratorViewState.Error)
            }
        }
    }

    suspend fun updateUserData(
        newName: String,
        newPhoneNumber: String,
        newEmail: String
    ) {
        _viewState.postValue(EditCollaboratorViewState.Loading)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        try {
            serviceFirebase.updateUser(
                userId,
                newName,
                newPhoneNumber,
                newEmail
            ).await()
            _viewState.postValue(EditCollaboratorViewState.Success)
        } catch (e: Exception) {
            _viewState.postValue(EditCollaboratorViewState.Error)
        }
    }

    suspend fun deleteUserImage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        try {
            serviceFirebase.deleteUserImage(userId).await()
        } catch (e: Exception) {
            _viewState.postValue(EditCollaboratorViewState.Error)
        }
    }

    suspend fun updateUserImage(image: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        try {
            serviceFirebase.setUserImage(userId, image).await()
        } catch (e: Exception) {
            _viewState.postValue(EditCollaboratorViewState.Error)
        }
    }

    data class CollaboratorData(
        val id: String,
        val user: User,
        val uri: Uri? = null
    )
}

sealed class EditCollaboratorViewState {
    object Success : EditCollaboratorViewState()
    object Error : EditCollaboratorViewState()
    object Loading : EditCollaboratorViewState()
}