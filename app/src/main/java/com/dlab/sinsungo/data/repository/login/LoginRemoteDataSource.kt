package com.dlab.sinsungo.data.repository.login

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginRemoteDataSource {
    /**
     * 로그인을 위한 RemoteDataSource
     * BASE_URL로 서버 주소
     * LoginService 인터페이스를 구현한 retrofit 인스터스 생성
     */
    private const val BASE_URL = "http://158.247.218.140:5001"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: LoginService = retrofit.create(LoginService::class.java)
}
