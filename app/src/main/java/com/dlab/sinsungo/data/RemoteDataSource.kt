package com.dlab.sinsungo.data

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RemoteDataSource {
    /**
     * RemoteDataSource
     * BASE_URL로 서버 주소
     * RetrofitService 인터페이스를 구현한 retrofit 인스터스 생성
     */
    private const val BASE_URL = "http://158.247.218.140:5001"
    private const val BARCODE_API_URL = "http://openapi.foodsafetykorea.go.kr/"

    private val gson = GsonBuilder().setLenient().create()
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val barcodeRetrofit = Retrofit.Builder()
        .baseUrl(BARCODE_API_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val service: RetrofitService = retrofit.create(RetrofitService::class.java)
    val barcodeService = barcodeRetrofit.create(BarcodeRetrofitService::class.java)
}
