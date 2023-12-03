package com.example.innovehair.view.createClientService

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.CapillaryProsthesis
import com.example.innovehair.service.models.CustomerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreateClientServiceViewModel : ViewModel() {
    private val _prosthesisList = MutableLiveData<List<CapillaryProsthesis>>()
    val prosthesisList: LiveData<List<CapillaryProsthesis>>
        get() = _prosthesisList

    private val _viewState = MutableLiveData<CreateClientServiceViewState>()
    val viewState: LiveData<CreateClientServiceViewState>
        get() = _viewState

    private val serviceFirebase = ServiceFirebase()

    init {
        viewModelScope.launch(Dispatchers.IO) {
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

    suspend fun createService(customerService: CustomerService) {
        _viewState.postValue(CreateClientServiceViewState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                serviceFirebase.createCustomerService(customerService)
                    .addOnSuccessListener {
                        _viewState.postValue(CreateClientServiceViewState.Success)
                    }
                    .await()
            } catch (e: Exception) {
                _viewState.postValue(CreateClientServiceViewState.Error)
                throw IllegalStateException(e.message)
            }
        }
    }

    sealed class CreateClientServiceViewState {
        object Success : CreateClientServiceViewState()
        object Error : CreateClientServiceViewState()
        object Loading : CreateClientServiceViewState()
    }
}