package com.example.innovehair.service.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class User(
    @get:PropertyName("isAdmin")
    @set:PropertyName("isAdmin")
    var isAdmin: Boolean? = false,
    val name: String = "",
    val phoneNumber: String = "",
    val email: String? = "",
    val creationDate: Date? = null
): Parcelable