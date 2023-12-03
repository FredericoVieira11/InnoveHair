package com.example.innovehair.view.collaboratorsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.User
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CollaboratorsListViewModel : ViewModel() {

    private val _collaboratorsList = MutableLiveData<List<CollaboratorData>>()
    val collaboratorsList: LiveData<List<CollaboratorData>>
        get() = _collaboratorsList

    private val _viewSate = MutableLiveData<CollaboratorsListViewState>()
    val viewState: LiveData<CollaboratorsListViewState>
        get() = _viewSate

    suspend fun getCollaboratorsList() {
        _viewSate.postValue(CollaboratorsListViewState.Loading)
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val serviceFirebase = ServiceFirebase()
                val querySnapshot = serviceFirebase.getAllCollaborators().await()

                val collaboratorsList = mutableListOf<CollaboratorData>()
                for (document in querySnapshot) {
                    val collaboratorItem = CollaboratorData(
                        document.reference.id,
                        User(
                            document.data["isAdmin"].toString().toBoolean(),
                            document.data["name"].toString(),
                            document.data["phoneNumber"].toString(),
                            document.data["email"].toString(),
                            (document.data["creationDate"] as Timestamp).toDate()
                        )
                    )
                    collaboratorsList.add(collaboratorItem)
                }
                val updatedCollaborators = collaboratorsList.map { collaboratorData ->
                    async(Dispatchers.IO) {
                        try {
                            val uri = ServiceFirebase().downloadImage(collaboratorData.id).await()
                            collaboratorData.copy(uri = uri)
                        } catch (e: Exception) {
                            collaboratorData.copy(uri = null)
                        }
                    }
                }.awaitAll()
                _viewSate.postValue(CollaboratorsListViewState.Success)
                _collaboratorsList.postValue(updatedCollaborators)
            } catch (e: java.lang.Exception) {
                _viewSate.postValue(CollaboratorsListViewState.Error)
            }
        }
    }
}

sealed class CollaboratorsListViewState {
    object Success : CollaboratorsListViewState()
    object Error : CollaboratorsListViewState()
    object Loading : CollaboratorsListViewState()
}

@Parcelize
data class CollaboratorData(
    val id: String,
    val user: User,
    val uri: Uri? = null
) : Parcelable