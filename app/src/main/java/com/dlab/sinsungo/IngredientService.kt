package com.dlab.sinsungo

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object IngredientService {
    private const val BASE_URL = "http://158.247.218.140:5001"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val client: IngredientAPI = retrofit.create(IngredientAPI::class.java)
}
