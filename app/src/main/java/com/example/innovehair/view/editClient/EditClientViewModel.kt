package com.example.innovehair.view.editClient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditClientViewModel : ViewModel() {

    private val _viewState = MutableLiveData<EditClientViewState>()
    val viewState: LiveData<EditClientViewState>
        get() = _viewState

    private val serviceFirebase = ServiceFirebase()

    suspend fun updateClient(clientId: String?, newData: Client) {
        _viewState.postValue(EditClientViewState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (clientId.isNullOrEmpty()) return@launch
                serviceFirebase.updateClient(clientId, newData).await()
                _viewState.postValue(EditClientViewState.Success)
            } catch (e: Exception) {
                _viewState.postValue(EditClientViewState.Error)
            }
        }
    }

    suspend fun associateClientToDifferentCollaborator(clientId: String?, newCollaboratorId: String) {
        _viewState.postValue(EditClientViewState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (clientId.isNullOrEmpty()) {
                    return@launch
                }

                // Passo 1: Obter os IDs dos Customer Services associados ao cliente
                val customerServiceQuery = serviceFirebase.getClientFromCustomerServices(clientId).await()

                // Obter IDs dos documentos
                val customerServiceIds = customerServiceQuery.documents.map { it.id }

                // Passo 2: Atualizar o collaboratorId em todos os Customer Services encontrados
                customerServiceIds.forEach { customerServiceId ->
                    serviceFirebase.updateCollaboratorIdForCustomerServices(customerServiceId, newCollaboratorId).await()
                }
                _viewState.postValue(EditClientViewState.Success)
            } catch (e: Exception) {
                _viewState.postValue(EditClientViewState.Error)
            }
        }
    }
}

sealed class EditClientViewState {
    object Success : EditClientViewState()
    object Error : EditClientViewState()
    object Loading : EditClientViewState()
}