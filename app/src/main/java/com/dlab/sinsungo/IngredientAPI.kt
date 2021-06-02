package com.dlab.sinsungo

import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface IngredientAPI {
    @GET("/refrigerator/ingredient/{id}")
    suspend fun getIngredient(
        @Path("id") refID: Int
    ): Response<List<IngredientModel>>

    // ocr 결과로 재료 추가 시
    @POST("/refrigerator/ingredient/")
    suspend fun postIngredient(
        @Body ingredientList: List<IngredientModel>
    ): Response<List<IngredientModel>>

    @DELETE("/refrigerator/ingredient/{id}")
    suspend fun deleteIngredient(
        @Path("id") ingredientID: Int?
    ): Response<JSONObject>

    @PUT("/refrigerator/ingredient/{id}")
    suspend fun putIngredient(
        @Path("id") refID: Int,
        @Body ingredientModel: IngredientModel
    ): Response<IngredientModel>

    @GET("/recipe/ingredient/")
    suspend fun getIngredientDict(): Response<List<IngredientDictModel>>
}
