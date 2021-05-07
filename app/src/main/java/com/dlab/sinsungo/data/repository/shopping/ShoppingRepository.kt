package com.dlab.sinsungo.data.repository.shopping

import com.dlab.sinsungo.data.model.Shopping

object ShoppingRepository {

    private val service = ShoppingRemoteDataSource.service

    suspend fun shopping(shopping: Shopping) = service.setShopping(shopping)

}
