package com.example.innovehair.view.collaboratorDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CollaboratorDetailsViewModel : ViewModel() {

    private val _viewState = MutableLiveData<CollaboratorDetailsViewState>()
    val viewState: LiveData<CollaboratorDetailsViewState>
        get() = _viewState

    private val _dates = MutableLiveData<Pair<Int?, Int?>>()
    val dates: LiveData<Pair<Int?, Int?>>
        get() = _dates

    private val serviceFirebase = ServiceFirebase()

    suspend fun getServiceHistoryByCollaboratorId(collaboratorId: String?) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                if (collaboratorId != null) {
                    val services = serviceFirebase.getServiceHistoryByCollaboratorId(collaboratorId).await()

                    val serviceDatesCount = services.count { it["scheduleServiceDate"] != null }
                    val applicationDatesCount = services.count { it["scheduleApplicationDate"] != null }

                    val countsPair = Pair(serviceDatesCount, applicationDatesCount)
                    _dates.postValue(countsPair)
                } else {
                    _dates.postValue(Pair(null, null))
                }
            } catch (e: Exception) {
                throw IllegalStateException(e.message)
            }
        }
    }

    suspend fun deleteAllCollaboratorData(collaboratorId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!collaboratorId.isNullOrEmpty()) {
                    serviceFirebase.deleteCollaborator(collaboratorId).await()

                    try {
                        serviceFirebase.deleteUserImage(collaboratorId).await()
                    } catch (e: Exception) {
                        // Do nothing
                    }

                    val services = try {
                        serviceFirebase.getServiceHistoryByCollaboratorId(collaboratorId).await()
                    } catch (e: Exception) {
                        emptyList()
                    }

                    if (services.iterator().hasNext()) {
                        services.forEach { service ->
                            val serviceId = service.id
                            serviceFirebase.deleteCustomerService(serviceId).await()
                            _viewState.postValue(CollaboratorDetailsViewState.Success)
                        }
                    } else {
                        _viewState.postValue(CollaboratorDetailsViewState.Success)
                    }
                } else {
                    _viewState.postValue(CollaboratorDetailsViewState.Error)
                }
            } catch (e: Exception) {
                _viewState.postValue(CollaboratorDetailsViewState.Error)
            }
        }
    }
}

sealed class CollaboratorDetailsViewState {
    object Success : CollaboratorDetailsViewState()
    object Error : CollaboratorDetailsViewState()
    object Loading : CollaboratorDetailsViewState()
}