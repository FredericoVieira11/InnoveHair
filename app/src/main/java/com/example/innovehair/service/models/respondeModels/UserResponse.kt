package com.example.innovehair.service.models.respondeModels

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UserResponse(
    val id: String,
    @get:PropertyName("isAdmin")
    var isAdmin: Boolean? = false,
    val name: String = "",
    val phoneNumber: String = "",
    val email: String? = "",
    val creationDate: Date? = null
): Parcelable
