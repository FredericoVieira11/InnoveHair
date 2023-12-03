package com.example.innovehair.view.search.searchForCollaborators

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.User
import com.example.innovehair.view.collaboratorsList.CollaboratorData
import com.example.innovehair.view.collaboratorsList.CollaboratorsListViewState
import com.google.firebase.Timestamp
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class SearchCollaboratorsViewModel : ViewModel() {

    private val _collaboratorsList = MutableLiveData<List<CollaboratorData>>()
    val collaboratorsList: LiveData<List<CollaboratorData>>
        get() = _collaboratorsList

    private val _viewSate = MutableLiveData<CollaboratorsListViewState>()
    val viewState: LiveData<CollaboratorsListViewState>
        get() = _viewSate

    init {
        viewModelScope.launch(Dispatchers.IO) {
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

                withContext(Dispatchers.Main) {
                    _collaboratorsList.postValue(updatedCollaborators)
                }
            } catch (e: Exception) {
                throw IllegalStateException(e.message)
            }
        }
    }
}