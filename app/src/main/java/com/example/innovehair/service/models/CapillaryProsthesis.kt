package com.example.innovehair.service.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CapillaryProsthesis(
    val id: String,
    val name: String? = "",
    val price: Double? = null
): Parcelable
