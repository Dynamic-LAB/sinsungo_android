package com.dlab.sinsungo

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface IngredientApi {
    @GET("/refrigerator/ingredient/{id}")
    suspend fun getIngredient(
        @Path("id") refID: Int
    ): Response<List<IngredientModel>>
}
