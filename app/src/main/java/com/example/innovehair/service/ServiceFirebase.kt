package com.example.innovehair.service

import android.net.Uri
import android.util.Log
import com.example.innovehair.service.models.Client
import com.example.innovehair.service.models.CustomerService
import com.example.innovehair.service.models.User
import com.example.innovehair.utils.Utils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await

class ServiceFirebase {

    //private val refFirebase = ServiceConfigFirebase().ref
    private val refFirestore = ServiceConfigFirebase().firestore
    private val refStorage = ServiceConfigFirebase().storage
    private val auth = FirebaseAuth.getInstance()

    // USERS
    private val usersCollection = refFirestore.collection(Utils.USERS)
    private val capillaryProthesisCollection = refFirestore.collection(Utils.CAPILLARY_PROTHESIS)
    private val clientsCollection = refFirestore.collection(Utils.CLIENTS)
    private val customerServiceCollection = refFirestore.collection(Utils.CUSTOMER_SERVICES)

    suspend fun checkEmailExists(email: String): Boolean {
        return try {
            val result = auth.fetchSignInMethodsForEmail(email).await()
            val signInMethods = result.signInMethods
            val exists = signInMethods?.isNotEmpty() ?: false
            Log.d("EmailExists", "Email: $email, Exists: $exists")
            exists
        } catch (e: Exception) {
            Log.e("EmailExists", "Error checking email existence: ${e.message}")
            false
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUser(userId: String): Task<DocumentSnapshot> {
        return usersCollection.document(userId).get()
    }

    fun setUser(userId: String, user: User) : Task<Void> {
        return usersCollection.document(userId).set(user)
    }

    fun deleteCollaborator(id: String): Task<Void> {
        return usersCollection.document(id).delete()
    }

    fun updateUser(userId: String, newName: String, newContact: String, newEmail: String): Task<Void> {
        val userData = mapOf(
            "name" to newName,
            "phoneNumber" to newContact,
            "email" to newEmail
        )

        return usersCollection.document(userId).update(userData)
    }

    fun createClient(client: Client): Task<DocumentReference> {
        return clientsCollection.add(client)
    }

    fun getAllCollaborators(): Task<QuerySnapshot> {
        return usersCollection
            .whereEqualTo("isAdmin", false)
            .get()
    }

    fun getClient(clientId: String): Task<DocumentSnapshot> {
        return clientsCollection.document(clientId).get()
    }

    fun deleteClient(id: String): Task<Void> {
        return clientsCollection.document(id).delete()
    }

    fun getCollaboratorClients(collaboratorId: String): Task<QuerySnapshot> {
        return clientsCollection
            .whereEqualTo("collaboratorId", collaboratorId)
            .get()
    }

    fun getAllClients(): Task<QuerySnapshot> {
        return clientsCollection.get()
    }

    fun updateClient(clientId: String, newData: Client): Task<Void> {
        val clientMap = mapOf(
            "name" to newData.name,
            "phoneNumber" to newData.phoneNumber,
            "email" to newData.email,
            "collaboratorId" to newData.collaboratorId
        )

        return clientsCollection.document(clientId).update(clientMap)
    }

    fun downloadImage(userId: String): Task<Uri> {
        return refStorage.child("users/$userId/profile.jpg").downloadUrl
    }

    fun setUserImage(userId: String, image: Uri): UploadTask {
        return refStorage.child("users/$userId/profile.jpg")
            .putFile(image)
    }

    fun deleteUserImage(userId: String): Task<Void> {
        return refStorage.child("users/$userId/profile.jpg")
            .delete()
    }

    fun getCapillaryProthesisList(): Task<QuerySnapshot> {
        return refFirestore.collection("CapillaryProthesis")
            .get()
    }

    fun getCapillaryProthesis(capillaryProsthesisId: String): Task<DocumentSnapshot> {
        return refFirestore.collection("CapillaryProthesis").document(capillaryProsthesisId).get()
    }

    fun addCapillaryProthesis(name: String, price: Double): Task<DocumentReference> {
        val capillaryProthesisData = hashMapOf(
            "name" to name,
            "price" to price
        )
        return refFirestore.collection("CapillaryProthesis")
            .add(capillaryProthesisData)
    }

    fun deleteCapillaryProthesis(documentId: String): Task<Void> {
        val documentReference = refFirestore.collection("CapillaryProthesis").document(documentId)
        return documentReference.delete()
    }

    fun updateCapillaryProthesis(documentId: String, updatedFields: Map<String, Any>): Task<Void> {
        val documentReference = refFirestore.collection("CapillaryProthesis").document(documentId)

        return documentReference.update(updatedFields)
    }

    fun createCustomerService(customerService: CustomerService): Task<DocumentReference> {
        return customerServiceCollection.add(customerService)
    }

    fun getClientFromCustomerServices(clientId: String): Task<QuerySnapshot> {
        return customerServiceCollection
            .whereEqualTo("clientId", clientId)
            .get()
    }

    fun updateCollaboratorIdForCustomerServices(customerServiceId: String, newCollaboratorId: String): Task<Void> {
        return customerServiceCollection
            .document(customerServiceId)
            .update("collaboratorId", newCollaboratorId)

    }

    fun getServiceHistoryByCollaboratorId(collaboratorId: String): Task<QuerySnapshot> {
        return customerServiceCollection
            .whereEqualTo("collaboratorId", collaboratorId)
            .get()
    }

    fun getAllServiceHistory(): Task<QuerySnapshot> {
        return customerServiceCollection
            .get()
    }

    fun updateCustomerService(id: String, customerService: CustomerService): Task<Void> {
        val newData = mapOf(
            "prothesisTypeId" to customerService.prothesisTypeId,
            "scheduleServiceDate" to customerService.scheduleServiceDate
        )

        return customerServiceCollection.document(id)
            .update(newData)
    }

    fun getServiceHistoryByCapillaryProthesisId(id: String): Task<QuerySnapshot> {
        return customerServiceCollection
            .whereEqualTo("prothesisTypeId", id)
            .get()
    }

    fun deleteCustomerService(id: String): Task<Void> {
        return customerServiceCollection.document(id).delete()
    }

    fun getCustomerServiceByClientId(id: String): Task<QuerySnapshot> {
        return customerServiceCollection
            .whereEqualTo("clientId", id)
            .orderBy("scheduleApplicationDate", Query.Direction.ASCENDING)
            .orderBy("scheduleServiceDate", Query.Direction.ASCENDING)
            .get()
    }

    fun searchServiceHistory(
        clientId: String?,
        collaboratorId: String?,
        prothesisTypeId: String?,
        customerServiceType: String?
    ): Task<QuerySnapshot> {
        var query: Query = customerServiceCollection

        if (!clientId.isNullOrEmpty()) {
            query = query.whereEqualTo("clientId", clientId)
        }

        if (!collaboratorId.isNullOrEmpty()) {
            query = query.whereEqualTo("collaboratorId", collaboratorId)
        }

        if (!prothesisTypeId.isNullOrEmpty()) {
            query = query.whereEqualTo("prothesisTypeId", prothesisTypeId)
        }

        if (!customerServiceType.isNullOrEmpty()) {
            query = query.whereEqualTo("customerServiceType", customerServiceType)
        }
        return query.get()
    }
}