package com.dlab.sinsungo.data.model

import com.google.gson.annotations.SerializedName

data class RecipeData(
    @SerializedName("recipes")
    var normal: ArrayList<Recipe>,

    @SerializedName("recommends")
    val recommend: ArrayList<Recipe>
)
