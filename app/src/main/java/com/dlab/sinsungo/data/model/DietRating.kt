package com.dlab.sinsungo.data.model

import com.google.gson.annotations.SerializedName

data class DietRating(
    @SerializedName("id")
    val id: Int,

    @SerializedName("ratings")
    val dietMemo: Map<String, Float>
)
