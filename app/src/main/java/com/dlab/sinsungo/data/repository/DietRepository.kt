package com.dlab.sinsungo.data.repository

import com.dlab.sinsungo.data.RemoteDataSource
import com.dlab.sinsungo.data.model.Diet

object DietRepository {
    private val service = RemoteDataSource.service
    suspend fun setDiet(diet: Diet) = service.setDiet(diet)
    suspend fun getDiet(refId: Int) = service.getDiet(refId)
    suspend fun editDiet(refId: Int, diets: List<Diet>) = service.editDiet(refId, diets)
    suspend fun delDiet(dietId: Int) = service.delDiet(dietId)
}
