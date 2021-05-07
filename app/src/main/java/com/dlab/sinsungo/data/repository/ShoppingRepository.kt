package com.dlab.sinsungo.data.repository

import com.dlab.sinsungo.data.RemoteDataSource
import com.dlab.sinsungo.data.model.Shopping

object ShoppingRepository {

    private val service = RemoteDataSource.service
    suspend fun setShopping(shopping: Shopping) = service.setShopping(shopping)
    suspend fun getShopping(refId: Int) = service.getShopping(refId)
    suspend fun editShopping(refId: Int, shopping: Shopping) = service.editShopping(refId, shopping)
    suspend fun delShopping(shopId: Int) = service.delShopping(shopId)

}
