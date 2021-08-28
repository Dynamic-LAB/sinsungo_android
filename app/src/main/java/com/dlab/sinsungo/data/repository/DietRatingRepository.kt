package com.dlab.sinsungo.data.repository

import com.dlab.sinsungo.data.RemoteDataSource
import com.dlab.sinsungo.data.model.Diet
import com.dlab.sinsungo.data.model.DietRating

object DietRatingRepository {
    private val service = RemoteDataSource.service
    suspend fun setDietRating(dietRating: DietRating) = service.setDietRating(dietRating)
}
