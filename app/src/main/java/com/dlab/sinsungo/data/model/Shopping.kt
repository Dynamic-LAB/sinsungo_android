package com.dlab.sinsungo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Shopping(
    @SerializedName("name")
    val shopName: String,

    @SerializedName("amount")
    val shopAmount: Int,

    @SerializedName("unit")
    val shopUnit: String,

    @SerializedName("memo")
    val shopMemo: String?,

    @SerializedName("id")
    val id: Int?
) : Parcelable
