package com.dlab.sinsungo.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int,

    val name: String,

    val thumbnail: String,

    val url: String,

    val description: String,

    val inRefIngredients: ArrayList<String?>,

    val notInRefIngredients: ArrayList<String?>
) : Parcelable
