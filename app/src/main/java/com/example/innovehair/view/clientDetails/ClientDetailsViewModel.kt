package com.example.innovehair.view.clientDetails

import androidx.lifecycle.*
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.User
import com.example.innovehair.view.collaboratorsList.CollaboratorData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ClientDetailsViewModel(
    private val id: String?
) : ViewModel() {

    private val _collaboratorData = MutableLiveData<CollaboratorData?>()
    val collaboratorData: LiveData<CollaboratorData?>
        get() = _collaboratorData

    private val _viewState = MutableLiveData<ClientDetailsViewState>()
    val viewState: LiveData<ClientDetailsViewState>
        get() = _viewState

    private val serviceFirebase = ServiceFirebase()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (id == null) return@launch
                val userRef = serviceFirebase.getUser(id).await()
                if (userRef.exists()) {
                    val id = userRef.id
                    val name = userRef.getString("name") ?: ""
                    val collaboratorObject = CollaboratorData(
                        id = id,
                        User(
                            name = name
                        )
                    )
                    withContext(Dispatchers.Main) {
                        _collaboratorData.value = collaboratorObject
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _collaboratorData.value = null
                    }
                }

            } catch (e: Exception) {
                throw IllegalStateException(e.message)
            }
        }
    }

    suspend fun deleteClient(id: String?) {
        _viewState.postValue(ClientDetailsViewState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (id != null) {
                    serviceFirebase.deleteClient(id).await()

                    val customerServiceQuery = serviceFirebase.getCustomerServiceByClientId(id).await()
                    for (document in customerServiceQuery.documents) {
                        serviceFirebase.deleteCustomerService(document.id).await()
                    }

                    _viewState.postValue(ClientDetailsViewState.Success)
                }
            } catch (e: Exception) {
                _viewState.postValue(ClientDetailsViewState.Error)
            }
        }
    }

    class ClientDetailsViewModelFactory(private val id: String?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClientDetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ClientDetailsViewModel(id) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class ClientDetailsViewState {
    object Success : ClientDetailsViewState()
    object Error : ClientDetailsViewState()
    object Loading : ClientDetailsViewState()
}