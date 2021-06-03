package com.dlab.sinsungo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class IngredientDictModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
) : Parcelable
