package com.dlab.sinsungo.data.repository.shopping

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ShoppingRemoteDataSource {
    private const val BASE_URL = "http://158.247.218.140:5001"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: ShoppingService = retrofit.create(ShoppingService::class.java)
}
