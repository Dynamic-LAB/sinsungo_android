package com.dlab.sinsungo.data.repository.login

import com.dlab.sinsungo.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    /**
     * 로그인을 위한 레트로핏 인터페이스
     * @GET, @POST, @PUT, @DELETE - HTTP METHODS 인자로는 라우터 경로 또는 Query Parameter
     * @Body로 request body로 User를 보냄
     * Response로 User를 받음
     * 코루틴 안에서 실행 가능하게 함수는 suspend로 정의
     */
    @POST("/user/auth/login")
    suspend fun login(
        @Body user: User
    ): Response<User>
}
