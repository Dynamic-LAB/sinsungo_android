package com.dlab.sinsungo

import com.google.gson.annotations.SerializedName

data class IngredientModel(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("name")
    val name: String,

    @SerializedName("amount")
    val count: Int,

    @SerializedName("expiration_date")
    val exdate: String,

    @SerializedName("category")
    val refCategory: String,

    @SerializedName("unit")
    val countType: String,

    @SerializedName("expiration_type")
    val exdateType: String
) {
}
