package com.example.innovehair.view.createNewCollaborator

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class CreateNewCollaboratorViewModel : ViewModel() {

    private val _viewState = MutableLiveData<CreateNewCollaboratorViewState>()
    val viewState: LiveData<CreateNewCollaboratorViewState>
        get() = _viewState

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri>
        get() = _imageUri

    suspend fun createNewCollaborator(
        image: Uri?,
        name: String,
        contact: String,
        email: String
    ) {
        _viewState.postValue(CreateNewCollaboratorViewState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, generateRandomPassword(20)).await()
                val userId = authResult.user?.uid ?: return@launch

                if (image != null) {
                    saveUserImage(image, userId)
                }
                val userData = User(
                    name = name,
                    isAdmin = false,
                    phoneNumber = contact,
                    email = email,
                    creationDate = Date()
                )
                val serviceFirebase = ServiceFirebase()
                serviceFirebase.setUser(userId, userData)
                    .addOnSuccessListener {
                        _viewState.postValue(CreateNewCollaboratorViewState.Success)
                    }
                    .addOnFailureListener {
                        _viewState.postValue(CreateNewCollaboratorViewState.Error)
                    }.await()
            } catch (e: Exception) {
                _viewState.postValue(CreateNewCollaboratorViewState.Error)
            }
        }
    }

    /*private suspend fun saveUserImage(
        imageUri: Uri,
        userId: String?
    ): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val storageRef = FirebaseStorage.getInstance().reference
                val imageRef = storageRef.child("users/$userId/profile.jpg")
                val uploadTask = imageRef.putFile(imageUri).await()
                return@withContext uploadTask.metadata?.reference?.downloadUrl?.await()
            } catch (e: Exception) {
                _viewState.postValue(CreateNewCollaboratorViewState.Error)
                return@withContext null
            }
        }
    }*/

    private suspend fun saveUserImage(
        imageUri: Uri,
        userId: String?
    ) {
        withContext(Dispatchers.IO) {
            try {
                val storageRef = FirebaseStorage.getInstance().reference
                val imageRef = storageRef.child("users/$userId/profile.jpg")
                imageRef.putFile(imageUri).await()
            } catch (e: Exception) {
                _viewState.postValue(CreateNewCollaboratorViewState.Error)
            }
        }
    }

    private fun generateRandomPassword(length: Int): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=<>?"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
}

sealed class CreateNewCollaboratorViewState {
    object Success : CreateNewCollaboratorViewState()
    object Error : CreateNewCollaboratorViewState()
    object Loading : CreateNewCollaboratorViewState()
}
