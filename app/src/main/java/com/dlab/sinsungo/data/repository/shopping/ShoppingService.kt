package com.dlab.sinsungo.data.repository.shopping

import com.dlab.sinsungo.data.model.Shopping
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.POST

interface ShoppingService {
    @POST("/shoppinglist")
    suspend fun setShopping(
        @Body shopping: Shopping
    ): Response<Shopping>
}
