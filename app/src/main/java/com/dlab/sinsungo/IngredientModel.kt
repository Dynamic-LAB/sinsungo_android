package com.dlab.sinsungo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class IngredientModel(
    @SerializedName("id")
    var id: Int?,

    @SerializedName("name")
    var name: String,

    @SerializedName("amount")
    var count: Int,

    @SerializedName("expiration_date")
    var exdate: String,

    @SerializedName("category")
    var refCategory: String,

    @SerializedName("unit")
    var countType: String,

    @SerializedName("expiration_type")
    var exDateType: String
) : Parcelable
