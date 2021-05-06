package com.dlab.sinsungo.data

import com.dlab.sinsungo.data.model.Recipe
import com.dlab.sinsungo.data.model.Shopping
import com.dlab.sinsungo.data.model.User
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
    @POST("/user/auth/login")
    suspend fun login(
        @Body user: User
    ): Response<User>

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

    // Shopping
    @POST("/shoppinglist")
    suspend fun setShopping(
        @Body shopping: Shopping
    ): Response<Shopping>

    @GET("/shoppinglist/{id}")
    suspend fun getShopping(
        @Path("id") refId: Int
    ): Response<ArrayList<Shopping>>

    @PUT("/shoppinglist/{id}")
    suspend fun editShopping(
        @Path("id") refId: Int,
        @Body shopping: Shopping
    ): Response<Shopping>

    @DELETE("/shoppinglist/{id}")
    suspend fun delShopping(
        @Path("id") shopId: Int
    ): Response<ArrayList<Shopping>>
}
