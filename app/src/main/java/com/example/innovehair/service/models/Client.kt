package com.example.innovehair.service.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Client(
    val name: String,
    val phoneNumber: String,
    val email: String,
    val collaboratorId: String? = ""
): Parcelable
