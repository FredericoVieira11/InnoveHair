package com.example.innovehair.view.clientsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.respondeModels.ClientResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClientsListViewModel : ViewModel() {

    private val _data = MutableLiveData<Pair<List<ClientResponse>, Boolean>>()
    val data: LiveData<Pair<List<ClientResponse>, Boolean>>
        get() = _data

    private val _viewState = MutableLiveData<ClientsListViewState>()
    val viewState: LiveData<ClientsListViewState>
        get() = _viewState

    /*init {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val serviceFirebase = ServiceFirebase()
                val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("Current User ID is null.")
                val userRef = serviceFirebase.getUser(currentUser).await()

                val isAdmin = if (userRef.exists()) {
                    userRef.getBoolean("isAdmin")
                } else {
                    false
                }

                if (isAdmin == true) {
                    val querySnapshot = serviceFirebase.getAllClients().await()
                    val clientsList = mutableListOf<ClientResponse>()
                    for (document in querySnapshot) {
                        val item = ClientResponse(
                            document.reference.id,
                            document.data["name"].toString(),
                            document.data["phoneNumber"].toString(),
                            document.data["email"].toString(),
                            document.data["collaboratorId"].toString()
                        )
                        clientsList.add(item)
                    }
                    _data.postValue(Pair(clientsList, isAdmin))
                } else {
                    val querySnapshot = serviceFirebase.getCollaboratorClients(currentUser).await()
                    val clientsList = mutableListOf<ClientResponse>()
                    for (document in querySnapshot) {
                        val item = ClientResponse(
                            document.reference.id,
                            document.data["name"].toString(),
                            document.data["phoneNumber"].toString(),
                            document.data["email"].toString(),
                            document.data["collaboratorId"].toString()
                        )
                        clientsList.add(item)
                    }
                    _data.postValue(Pair(clientsList, isAdmin ?: false))
                }
            } catch (e: Exception) {
                throw IllegalStateException(e.message)
            }
        }
    }*/

    fun loadData() {
        _viewState.postValue(ClientsListViewState.Loading)
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val serviceFirebase = ServiceFirebase()
                val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val userRef = serviceFirebase.getUser(currentUser).await()

                val isAdmin = if (userRef.exists()) {
                    userRef.getBoolean("isAdmin")
                } else {
                    false
                }

                if (isAdmin == true) {
                    val querySnapshot = serviceFirebase.getAllClients().await()
                    val clientsList = mutableListOf<ClientResponse>()
                    for (document in querySnapshot) {
                        val item = ClientResponse(
                            document.reference.id,
                            document.data["name"].toString(),
                            document.data["phoneNumber"].toString(),
                            document.data["email"].toString(),
                            document.data["collaboratorId"].toString()
                        )
                        clientsList.add(item)
                    }
                    _data.postValue(Pair(clientsList, isAdmin))
                    _viewState.postValue(ClientsListViewState.Success)
                } else {
                    val querySnapshot = serviceFirebase.getCollaboratorClients(currentUser).await()
                    val clientsList = mutableListOf<ClientResponse>()
                    for (document in querySnapshot) {
                        val item = ClientResponse(
                            document.reference.id,
                            document.data["name"].toString(),
                            document.data["phoneNumber"].toString(),
                            document.data["email"].toString(),
                            document.data["collaboratorId"].toString()
                        )
                        clientsList.add(item)
                    }
                    _data.postValue(Pair(clientsList, isAdmin ?: false))
                    _viewState.postValue(ClientsListViewState.Success)
                }
            } catch (e: Exception) {
                _viewState.postValue(ClientsListViewState.Error)
            }
        }
    }

}

sealed class ClientsListViewState {
    object Success : ClientsListViewState()
    object Error : ClientsListViewState()
    object Loading : ClientsListViewState()
}