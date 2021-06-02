package com.dlab.sinsungo.data

import android.app.Notification
import com.dlab.sinsungo.data.model.*
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {
    /**
     * 레트로핏 인터페이스
     * @GET, @POST, @PUT, @DELETE - HTTP METHODS 인자로는 라우터 경로 또는 Query Parameter
     * @Body로 request body로 User를 보냄
     * Response로 모델 객체를 받음
     * 코루틴 안에서 실행 가능하게 함수는 suspend로 정의
     */

    // User
    @POST("/user")
    suspend fun getUser(
        @Body user: User
    ): Response<User>

    @POST("/user/auth/login")
    suspend fun login(
        @Body user: User
    ): Response<User>

    @PUT("/user")
    suspend fun updateUser(
        @Body user: User
    ): Response<User>

    @PUT("/user/invite")
    suspend fun inviteUser(
        @Body body: HashMap<String, Any>
    ): Response<User>

    @HTTP(method = "DELETE", path = "/user", hasBody = true)
    suspend fun deleteUser(
        @Body user: User
    ): Response<JsonObject>

    // Recipe
    @GET("recipe/{id}")
    suspend fun getRecipe(
        @Path("id") id: Int,
        @Query("start") start: Int,
        @Query("end") end: Int
    ): Response<ArrayList<Recipe>>

    @GET("search/recipe/{id}")
    suspend fun searchRecipe(
        @Path("id") id: Int,
        @Query("start") start: Int,
        @Query("end") end: Int,
        @Query("query") query: String
    ): Response<ArrayList<Recipe>>

    // Refrigerator
    @GET("refrigerator/{id}")
    suspend fun getRefrigerator(
        @Path("id") id: Int
    ): Response<Refrigerator>

    @POST("/refrigerator")
    suspend fun createRefrigerator(
        @Body body: HashMap<String, Any>
    ): Response<JsonObject>

    // Shopping
    @POST("/shoppinglist")
    suspend fun setShopping(
        @Body shopping: Shopping
    ): Response<Shopping>

    @GET("/shoppinglist/{id}")
    suspend fun getShopping(
        @Path("id") refId: Int
    ): Response<List<Shopping>>

    @PUT("/shoppinglist/{id}")
    suspend fun editShopping(
        @Path("id") refId: Int,
        @Body shopping: Shopping
    ): Response<Shopping>

    @DELETE("/shoppinglist/{id}")
    suspend fun delShopping(
        @Path("id") shopId: Int
    ): Response<JsonObject>

    // Diet (식단)
    @GET("/diet/refrigerator/{id}")
    suspend fun getDiet(
        @Path("id") refId: Int
    ): Response<List<Diet>>

    @POST("/diet")
    suspend fun setDiet(
        @Body diet: Diet
    ): Response<Diet>

    @PUT("/diet/{id}")
    suspend fun editDiet(
        @Path("id") refId: Int,
        @Body diets: List<Diet>
    ): Response<Diet>

    @DELETE("/diet/{id}")
    suspend fun delDiet(
        @Path("id") dietId: Int
    ): Response<JsonObject>

    // Notice
    @GET("/notice")
    suspend fun getNotice(): Response<List<Notice>>

    // Notification
    @GET("/notification/{id}")
    suspend fun getNotification(
        @Path("id") refId: Int
    ): Response<List<NotificationModel>>
}
