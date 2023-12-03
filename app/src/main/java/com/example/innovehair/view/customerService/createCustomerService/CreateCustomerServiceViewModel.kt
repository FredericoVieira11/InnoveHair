package com.example.innovehair.view.customerService.createCustomerService

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.CapillaryProsthesis
import com.example.innovehair.service.models.Client
import com.example.innovehair.service.models.CustomerService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreateCustomerServiceViewModel : ViewModel() {

    private val _viewState = MutableLiveData<CreateCustomerServiceViewState>()
    val viewState: LiveData<CreateCustomerServiceViewState>
        get() = _viewState

    private val _prosthesisList = MutableLiveData<List<CapillaryProsthesis>>()
    val prosthesisList: LiveData<List<CapillaryProsthesis>>
        get() = _prosthesisList

    private val serviceFirebase = ServiceFirebase()

    init {
        getAllCapillaryProthesis()
    }

    private fun getAllCapillaryProthesis() {
        viewModelScope.launch {
            serviceFirebase.getCapillaryProthesisList()
                .addOnSuccessListener {
                    val prosthesisList = mutableListOf<CapillaryProsthesis>()
                    for (document in it) {
                        val id = document.id
                        val name = document.getString("name") ?: ""
                        val price = document.getDouble("price") ?: 0.0
                        val prosthesis = CapillaryProsthesis(id, name, price)
                        prosthesisList.add(prosthesis)
                    }
                    _prosthesisList.value = prosthesisList
                }
        }
    }

    suspend fun registerCustomerService(client: Client, customerService: CustomerService) {
        _viewState.postValue(CreateCustomerServiceViewState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val collaboratorId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val updateClient = client.copy(collaboratorId = collaboratorId)
                val clientReference = serviceFirebase.createClient(updateClient)
                    .addOnFailureListener {
                        _viewState.postValue(CreateCustomerServiceViewState.Error)
                    }
                    .await()
                val clientId = clientReference.id
                val customerServiceWithClientId = customerService.copy(clientId = clientId, collaboratorId = collaboratorId)
                serviceFirebase.createCustomerService(customerServiceWithClientId)
                    .addOnSuccessListener {
                        _viewState.postValue(CreateCustomerServiceViewState.Success)
                    }
                    .await()
            } catch (e: Exception) {
                _viewState.postValue(CreateCustomerServiceViewState.Error)
            }
        }
    }
}

sealed class CreateCustomerServiceViewState {
    object Success : CreateCustomerServiceViewState()
    object Error : CreateCustomerServiceViewState()
    object Loading : CreateCustomerServiceViewState()
}