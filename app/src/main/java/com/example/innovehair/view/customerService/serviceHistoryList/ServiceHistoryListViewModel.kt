package com.example.innovehair.view.customerService.serviceHistoryList

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.innovehair.service.ServiceFirebase
import com.example.innovehair.service.models.CapillaryProsthesis
import com.example.innovehair.service.models.User
import com.example.innovehair.service.models.respondeModels.ClientResponse
import com.example.innovehair.service.models.respondeModels.CustomerServiceResponse
import com.example.innovehair.service.models.respondeModels.UserResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ServiceHistoryListViewModel : ViewModel() {

    private val _data = MutableLiveData<Pair<List<CustomerServiceData>, Boolean>>()
    val data: LiveData<Pair<List<CustomerServiceData>, Boolean>>
        get() = _data

    private val _prosthesisList = MutableLiveData<List<CapillaryProsthesis>>()
    val prosthesisList: LiveData<List<CapillaryProsthesis>>
        get() = _prosthesisList

    private val _collaboratorList = MutableLiveData<MutableList<UserResponse>>()
    val collaboratorList: LiveData<MutableList<UserResponse>>
        get() = _collaboratorList

    private val _clientsList = MutableLiveData<MutableList<ClientResponse?>>()
    val clientsList: LiveData<MutableList<ClientResponse?>>
        get() = _clientsList

    private val _viewState = MutableLiveData<ServiceHistoryListViewState>()
    val viewState: LiveData<ServiceHistoryListViewState>
        get() = _viewState

    private val serviceFirebase = ServiceFirebase()

    suspend fun getServicesHistoryList() {
        _viewState.postValue(ServiceHistoryListViewState.Loading)
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("Collaborator ID is null")
                val userRef = serviceFirebase.getUser(currentUser).await()

                val isAdmin = if (userRef.exists()) {
                    userRef.getBoolean("isAdmin")
                } else {
                    false
                }

                val querySnapshot = if (isAdmin == true) {
                    serviceFirebase.getAllServiceHistory().await()
                } else {
                    serviceFirebase.getServiceHistoryByCollaboratorId(currentUser).await()
                }

                val clientsList = mutableListOf<ClientResponse?>()
                val collaboratorsList = mutableListOf<UserResponse>()
                val prothesisList = mutableListOf<CapillaryProsthesis>()

                val customerServiceDataList = querySnapshot.documents.mapNotNull { document ->
                    val clientId = document.getString("clientId")
                    val collaboratorId = document.getString("collaboratorId")
                    val prothesisTypeId = document.getString("prothesisTypeId")

                    if (clientId != null && collaboratorId != null && prothesisTypeId != null) {
                        // Use coroutines para esperar as consultas aos Firestore
                        val clientResponse = serviceFirebase.getClient(clientId).await()
                        val collaboratorResponse = serviceFirebase.getUser(collaboratorId).await()
                        val prothesisResponse = serviceFirebase.getCapillaryProthesis(prothesisTypeId).await()
                        val customerServiceResponse = document.toObject(CustomerServiceResponse::class.java)

                        // Verifique se os documentos existem antes de acessar os dados
                        if (clientResponse.exists() && collaboratorResponse.exists() && prothesisResponse.exists() && customerServiceResponse != null) {
                            val clientObject = clientResponse.toObject(ClientResponse::class.java)
                            val collaboratorObject = UserResponse(
                                id = "",
                                isAdmin = collaboratorResponse.getBoolean("isAdmin"),
                                name = collaboratorResponse.getString("name").toString(),
                                phoneNumber = collaboratorResponse.getString("phoneNumber").toString(),
                                email = collaboratorResponse.getString("email").toString(),
                                creationDate = collaboratorResponse.getDate("creationDate")
                            )
                            val prothesisObject = CapillaryProsthesis(
                                id = "",
                                name = prothesisResponse.getString("name").toString(),
                                price = prothesisResponse.getDouble("price")
                            )

                            // Crie instâncias dos objetos com os IDs
                            val clientData = clientObject?.copy(id = clientResponse.id)
                            val collaboratorData = collaboratorObject.copy(id = collaboratorResponse.id)
                            val prothesisData = prothesisObject.copy(id = prothesisResponse.id)
                            val customerServiceData = customerServiceResponse.copy(id = document.reference.id)

                            clientsList.add(clientData)
                            collaboratorsList.add(collaboratorData)
                            prothesisList.add(prothesisData)
                            // Crie a instância final do CustomerServiceData
                            CustomerServiceData(clientData, collaboratorData, prothesisData, customerServiceData)
                        } else {
                            _viewState.postValue(ServiceHistoryListViewState.Error)
                            null
                        }
                    } else {
                        _viewState.postValue(ServiceHistoryListViewState.Error)
                        null
                    }
                }.sortedWith(compareBy
                // Organize pelo dia mais próximo entre scheduleServiceDate e scheduleApplicationDate
                {
                    minOf(
                        it.customerServiceResponse?.scheduleServiceDate?.time
                            ?: Long.MAX_VALUE,
                        it.customerServiceResponse?.scheduleApplicationDate?.time
                            ?: Long.MAX_VALUE
                    )
                })
                _data.postValue(Pair(customerServiceDataList, isAdmin ?: false))
                _clientsList.postValue(clientsList)
                _collaboratorList.postValue(collaboratorsList)
                _prosthesisList.postValue(prothesisList)
                _viewState.postValue(ServiceHistoryListViewState.Success)
            } catch (e: Exception) {
                _viewState.postValue(ServiceHistoryListViewState.Error)
            }
        }
    }

    suspend fun searchServiceHistory(
        clientId: String,
        collaboratorId: String,
        prothesisTypeId: String,
        customerServiceType: String
    ) {
        _viewState.postValue(ServiceHistoryListViewState.Success)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("Collaborator ID is null")
                val userRef = serviceFirebase.getUser(currentUserId).await()

                val isAdmin = if (userRef.exists()) {
                    userRef.getBoolean("isAdmin")
                } else {
                    false
                }

                val querySnapshot = if (isAdmin == false) {
                    serviceFirebase.searchServiceHistory(
                        clientId,
                        currentUserId,
                        prothesisTypeId,
                        customerServiceType
                    ).await()
                } else {
                    serviceFirebase.searchServiceHistory(
                        clientId,
                        collaboratorId,
                        prothesisTypeId,
                        customerServiceType
                    ).await()
                }

                val customerServiceDataList = querySnapshot.documents.mapNotNull { document ->
                    val clientId = document.getString("clientId")
                    val collaboratorId = document.getString("collaboratorId")
                    val prothesisTypeId = document.getString("prothesisTypeId")

                    if (clientId != null && collaboratorId != null && prothesisTypeId != null) {
                        val clientResponse = serviceFirebase.getClient(clientId).await()
                        val collaboratorResponse = serviceFirebase.getUser(collaboratorId).await()
                        val prothesisResponse = serviceFirebase.getCapillaryProthesis(prothesisTypeId).await()
                        val customerServiceResponse = document.toObject(CustomerServiceResponse::class.java)

                        if (clientResponse.exists() && collaboratorResponse.exists() && prothesisResponse.exists() && customerServiceResponse != null) {
                            val clientObject = clientResponse.toObject(ClientResponse::class.java)
                            val collaboratorObject = UserResponse(
                                id = "",
                                isAdmin = collaboratorResponse.getBoolean("isAdmin"),
                                name = collaboratorResponse.getString("name").toString(),
                                phoneNumber = collaboratorResponse.getString("phoneNumber").toString(),
                                email = collaboratorResponse.getString("email").toString(),
                                creationDate = collaboratorResponse.getDate("creationDate")
                            )
                            val prothesisObject = CapillaryProsthesis(
                                id = "",
                                name = prothesisResponse.getString("name").toString(),
                                price = prothesisResponse.getDouble("price")
                            )

                            // Crie instâncias dos objetos com os IDs
                            val clientData = clientObject?.copy(id = clientResponse.id)
                            val collaboratorData = collaboratorObject.copy(id = collaboratorResponse.id)
                            val prothesisData = prothesisObject.copy(id = prothesisResponse.id)
                            val customerServiceData = customerServiceResponse.copy(id = document.reference.id)

                            // Crie a instância final do CustomerServiceData
                            CustomerServiceData(clientData, collaboratorData, prothesisData, customerServiceData)
                        } else {
                            _viewState.postValue(ServiceHistoryListViewState.Error)
                            null
                        }
                    } else {
                        _viewState.postValue(ServiceHistoryListViewState.Error)
                        null
                    }
                }.sortedWith(compareBy
                // Organize pelo dia mais próximo entre scheduleServiceDate e scheduleApplicationDate
                {
                    minOf(
                        it.customerServiceResponse?.scheduleServiceDate?.time
                            ?: Long.MAX_VALUE,
                        it.customerServiceResponse?.scheduleApplicationDate?.time
                            ?: Long.MAX_VALUE
                    )
                })
                _data.postValue(Pair(customerServiceDataList, isAdmin ?: false))
                _viewState.postValue(ServiceHistoryListViewState.Success)
            } catch (e: Exception) {
                _viewState.postValue(ServiceHistoryListViewState.Error)
            }
        }
    }
}

sealed class ServiceHistoryListViewState {
    object Success : ServiceHistoryListViewState()
    object Error : ServiceHistoryListViewState()
    object Loading : ServiceHistoryListViewState()
}

@Parcelize
data class CustomerServiceData(
    val client: ClientResponse?,
    val collaborator: UserResponse?,
    val prothesisType: CapillaryProsthesis?,
    val customerServiceResponse: CustomerServiceResponse?
): Parcelable