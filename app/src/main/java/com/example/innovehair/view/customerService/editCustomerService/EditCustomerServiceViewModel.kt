package com.example.innovehair.view.customerService.editCustomerService

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

class EditCustomerServiceViewModel : ViewModel() {

    private val _viewState = MutableLiveData<EditCustomerServiceViewState>()
    val viewSate: LiveData<EditCustomerServiceViewState>
        get() = _viewState

    private val _prosthesisList = MutableLiveData<List<CapillaryProsthesis>>()
    val prosthesisList: LiveData<List<CapillaryProsthesis>>
        get() = _prosthesisList

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

    suspend fun updateCustomerService(id: String?, customerService: CustomerService) {
        _viewState.postValue(EditCustomerServiceViewState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (id != null) {
                    serviceFirebase.updateCustomerService(
                        id,
                        customerService
                    ).await()
                    _viewState.postValue(EditCustomerServiceViewState.Success)
                } else {
                    _viewState.postValue(EditCustomerServiceViewState.Error)
                }
            } catch (e: Exception) {
                _viewState.postValue(EditCustomerServiceViewState.Error)
            }
        }
    }
}

sealed class EditCustomerServiceViewState {
    object Success : EditCustomerServiceViewState()
    object Error : EditCustomerServiceViewState()
    object Loading : EditCustomerServiceViewState()
}