package com.dlab.sinsungo.data.model

import com.google.gson.annotations.SerializedName

data class NotificationModel(
    @SerializedName("content")
    val content: String
)
