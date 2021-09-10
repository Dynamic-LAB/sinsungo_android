package com.dlab.sinsungo.data.repository

import com.dlab.sinsungo.data.RemoteDataSource
import com.dlab.sinsungo.data.model.IngredientModel

object IngredientRepository {
    private val client = RemoteDataSource.service

    suspend fun getIngredient(refID: Int) = client.getIngredient(refID)
    suspend fun deleteIngredient(ingredientID: Int?) = client.deleteIngredient(ingredientID)
    suspend fun putIngredient(refID: Int, ingredientModel: IngredientModel) =
        client.putIngredient(refID, ingredientModel)

    suspend fun postIngredient(ingredientList: List<IngredientModel>) = client.postIngredient(ingredientList)

    suspend fun getIngredientDict() = client.getIngredientDict()
}
