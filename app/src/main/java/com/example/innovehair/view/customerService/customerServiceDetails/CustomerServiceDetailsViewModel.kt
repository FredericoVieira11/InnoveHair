package com.example.innovehair.view.customerService.customerServiceDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CustomerServiceDetailsViewModel : ViewModel() {

    private val _viewState = MutableLiveData<CustomerServiceDetailsState>()
    val viewState: LiveData<CustomerServiceDetailsState>
        get() = _viewState

    private val serviceFirebase = ServiceFirebase()

    suspend fun deleteCustomerService(id: String?) {
        _viewState.postValue(CustomerServiceDetailsState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (id != null) {
                    serviceFirebase.deleteCustomerService(id).await()
                    _viewState.postValue(CustomerServiceDetailsState.Success)
                }
            } catch (e: Exception) {
                _viewState.postValue(CustomerServiceDetailsState.Error)
                throw IllegalStateException(e.message)
            }
        }
    }
}

sealed class CustomerServiceDetailsState {
    object Success : CustomerServiceDetailsState()
    object Error : CustomerServiceDetailsState()
    object Loading : CustomerServiceDetailsState()
}