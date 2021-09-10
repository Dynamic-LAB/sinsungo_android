package com.dlab.sinsungo.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notice(
    val title: String,

    val content: String,

    val date: String
) : Parcelable
