package com.dlab.sinsungo.data.repository

import com.dlab.sinsungo.data.RemoteDataSource
import com.dlab.sinsungo.data.model.Recipe

object RecipeRepository {
    /**
     * 레시피를 위한 Repository
     * RemoteDataSource의 retrofit 인스턴스 service에 초기화
     * getRecipe 함수 호출 시 RetrofitService의 getRecipe 함수 호출해 네트워크 통신 수행
     */
    private val service = RemoteDataSource.service

    suspend fun getRecipe(id: Int, start: Int, end: Int) = service.getRecipe(id, start, end)
    suspend fun searchRecipe(id: Int, start: Int, end: Int, query: String) = service.searchRecipe(id, start, end, query)
}
