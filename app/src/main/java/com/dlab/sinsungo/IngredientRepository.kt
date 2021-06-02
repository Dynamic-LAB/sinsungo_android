package com.dlab.sinsungo

object IngredientRepository {
    private val client = IngredientService.client

    suspend fun getIngredient(refID: Int) = client.getIngredient(refID)
    suspend fun deleteIngredient(ingredientID: Int?) = client.deleteIngredient(ingredientID)
    suspend fun putIngredient(refID: Int, ingredientModel: IngredientModel) =
        client.putIngredient(refID, ingredientModel)

    suspend fun postIngredient(ingredientList: List<IngredientModel>) = client.postIngredient(ingredientList)

    suspend fun getIngredientDict() = client.getIngredientDict()
}
