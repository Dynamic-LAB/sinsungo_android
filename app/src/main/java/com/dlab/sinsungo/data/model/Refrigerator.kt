package com.dlab.sinsungo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Refrigerator(
    @SerializedName("master")
    val master: String,

    @SerializedName("invite_key")
    val inviteKey: String,

    @SerializedName("members")
    val members: ArrayList<User>?
) : Parcelable
