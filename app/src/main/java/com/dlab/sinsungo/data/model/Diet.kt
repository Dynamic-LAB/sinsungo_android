package com.dlab.sinsungo.data.model

import com.dlab.sinsungo.IngredientModel
import com.google.gson.annotations.SerializedName

data class Diet(
    @SerializedName("id")
    val id: Int,

    @SerializedName("memo")
    val dietMemo: Int,

    @SerializedName("date")
    val dietDate: String,

    @SerializedName("memo")
    val dietMenus: List<String>,

    @SerializedName("ingredients")
    val dietIngredients: List<IngredientModel>
)
