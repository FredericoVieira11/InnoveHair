package com.example.innovehair.service.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class CustomerService(
    val clientId: String? = "",
    val collaboratorId: String? = "",
    val prothesisTypeId: String? = "",
    val customerServiceType: String? = "",
    val scheduleServiceDate: Date? = null,
    val width: Double? = null,
    val length: Double? = null,
    val capillaryDensity: Int? = null,
    val hairColour: String? = "",
    val hairTexture: String? = "",
    val scheduleApplicationDate: Date? = null,
    val allergies: String? = "",
    val createdAt: Date? = null
): Parcelable
