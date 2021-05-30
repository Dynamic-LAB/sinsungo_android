package com.dlab.sinsungo.data.repository

import com.dlab.sinsungo.data.RemoteDataSource
import com.dlab.sinsungo.data.model.Refrigerator

object RefrigeratorRepository {
    /**
     * 냉장고 정보 및 멤버를 위한 Repository
     * RemoteDataSource의 retrofit 인스턴스 service에 초기화
     * getRefrigerator 함수 호출 시 RetrofitService의 getRefrigerator 함수 호출해 네트워크 통신 수행
     */
    private val service = RemoteDataSource.service

    suspend fun getRefrigerator(id: Int) = service.getRefrigerator(id)
    suspend fun createRefrigerator(body: HashMap<String, Any>) = service.createRefrigerator(body)
}
