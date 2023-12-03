package com.example.innovehair.service.models.respondeModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClientResponse(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val collaboratorId: String = ""
): Parcelable
