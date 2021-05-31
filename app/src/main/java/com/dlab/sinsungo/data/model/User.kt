package com.dlab.sinsungo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("id")
    val userId: String,

    @SerializedName("login_type")
    val loginType: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("push_token")
    val pushToken: String?,

    @SerializedName("refrigerator_id")
    val refId: Int?,

    @SerializedName("push_setting")
    val pushSetting: Int
) : Parcelable
