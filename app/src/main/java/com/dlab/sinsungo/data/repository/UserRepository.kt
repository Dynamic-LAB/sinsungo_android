package com.dlab.sinsungo.data.repository

import com.dlab.sinsungo.data.RemoteDataSource
import com.dlab.sinsungo.data.model.User

object UserRepository {
    /**
     * 로그인을 위한 Repository
     * RemoteDataSource의 retrofit 인스턴스 service에 초기화
     * login 함수 호출 시 RetrofitService의 login 함수 호출해 네트워크 통신 수행
     */
    private val service = RemoteDataSource.service

    suspend fun getUser(user: User) = service.getUser(user)
    suspend fun login(user: User) = service.login(user)
    suspend fun update(user: User) = service.updateUser(user)
    suspend fun invite(body: HashMap<String, Any>) = service.inviteUser(body)
    suspend fun delete(user: User) = service.deleteUser(user)
}
