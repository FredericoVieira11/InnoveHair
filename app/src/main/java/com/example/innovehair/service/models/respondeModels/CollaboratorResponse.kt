package com.example.innovehair.service.models.respondeModels

import android.net.Uri
import android.os.Parcelable
import com.example.innovehair.service.models.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CollaboratorResponse(
    val id: String,
    val user: User,
    val uri: Uri? = null
): Parcelable
