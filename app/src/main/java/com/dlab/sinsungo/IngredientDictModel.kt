package com.dlab.sinsungo

import com.google.gson.annotations.SerializedName

data class IngredientDictModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
)
