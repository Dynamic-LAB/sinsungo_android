package com.dlab.sinsungo

object IngredientRepository {
    private val client = IngredientService.client

    suspend fun getIngredient(refID: Int) = client.getIngredient(refID)
    // suspend fun postIngredient(ingredientModel: IngredientModel) = client.postIngredient(ingredientModel)
}
