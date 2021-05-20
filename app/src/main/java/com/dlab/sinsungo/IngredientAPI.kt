package com.dlab.sinsungo

import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface IngredientAPI {
    @GET("/refrigerator/ingredient/{id}")
    suspend fun getIngredient(
        @Path("id") refID: Int
    ): Response<List<IngredientModel>>

    @POST("/refrigerator/ingredient/")
    suspend fun postIngredient(
        @Body ingredientModel: IngredientModel
    ): Response<IngredientModel>

    @DELETE("/refrigerator/ingredient/{id}")
    suspend fun deleteIngredient(
        @Path("id") ingredientID: Int?
    ): Response<JSONObject>

    @PUT("/refrigerator/ingredient/{id}")
    suspend fun putIngredient(
        @Path("id") refID: Int,
        @Body ingredientModel: IngredientModel
    ): Response<IngredientModel>
}
