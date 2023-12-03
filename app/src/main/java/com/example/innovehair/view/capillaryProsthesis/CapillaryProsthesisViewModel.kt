package com.example.innovehair.view.capillaryProsthesis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.CapillaryProsthesis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CapillaryProsthesisViewModel : ViewModel() {

    private val _viewState = MutableLiveData<CapillaryProsthesisViewState>()
    val viewState: LiveData<CapillaryProsthesisViewState>
        get() = _viewState

    private val _prosthesisList = MutableLiveData<List<CapillaryProsthesis>>()
    val prosthesisList: LiveData<List<CapillaryProsthesis>>
        get() = _prosthesisList

    private val serviceFirebase = ServiceFirebase()

    init {
        getAllCapillaryProthesis()
    }

    private fun getAllCapillaryProthesis() {
        _viewState.postValue(CapillaryProsthesisViewState.Loading)
        viewModelScope.launch(Dispatchers.Main) {
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
                    _viewState.postValue(CapillaryProsthesisViewState.Success)
                }
                .addOnFailureListener {
                    _viewState.postValue(CapillaryProsthesisViewState.Error)
                }.await()
        }
    }

    suspend fun createCapillaryProthesis(name: String, price: Double) {
        _viewState.postValue(CapillaryProsthesisViewState.Loading)
        serviceFirebase.addCapillaryProthesis(name, price)
            .addOnSuccessListener {
                getAllCapillaryProthesis()
                _viewState.postValue(CapillaryProsthesisViewState.Success)
            }
            .addOnFailureListener {
                _viewState.postValue(CapillaryProsthesisViewState.Error)
            }.await()
    }

    suspend fun deleteCapillaryProthesis(prothesisId: String) {
        _viewState.postValue(CapillaryProsthesisViewState.Loading)
        serviceFirebase.deleteCapillaryProthesis(prothesisId)
            .addOnSuccessListener {
                getAllCapillaryProthesis()
                _viewState.postValue(CapillaryProsthesisViewState.Success)
            }.await()

        val services = try {
            serviceFirebase.getServiceHistoryByCapillaryProthesisId(prothesisId).await()
        } catch (e: Exception) {
            emptyList()
        }

        if (services.iterator().hasNext()) {
            services.forEach { service ->
                val serviceId = service.id
                serviceFirebase.deleteCustomerService(serviceId).await()
                _viewState.postValue(CapillaryProsthesisViewState.Success)
            }
        } else {
            _viewState.postValue(CapillaryProsthesisViewState.Success)
        }
    }

    suspend fun updateCapillaryProthesis(
        prothesisId: String,
        name: String,
        price: Double
    ) {
        val capillaryProthesisData = hashMapOf(
            "name" to name,
            "price" to price
        )
        _viewState.postValue(CapillaryProsthesisViewState.Loading)
        serviceFirebase.updateCapillaryProthesis(prothesisId, capillaryProthesisData)
            .addOnSuccessListener {
                getAllCapillaryProthesis()
                _viewState.postValue(CapillaryProsthesisViewState.Success)
            }
            .addOnFailureListener {
                _viewState.postValue(CapillaryProsthesisViewState.Error)
            }.await()
    }
}

sealed class CapillaryProsthesisViewState {
    object Success : CapillaryProsthesisViewState()
    object Error : CapillaryProsthesisViewState()
    object Loading : CapillaryProsthesisViewState()
}