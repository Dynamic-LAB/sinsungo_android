package com.dlab.sinsungo

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IngredientAPI {
    @GET("/refrigerator/ingredient/{id}")
    suspend fun getIngredient(
        @Path("id") refID: Int
    ): Response<List<IngredientModel>>

    @POST("/refrigerator/ingredient/")
    suspend fun postIngredient(
        @Body ingredientModel: IngredientModel
    ): Response<IngredientModel>
}
