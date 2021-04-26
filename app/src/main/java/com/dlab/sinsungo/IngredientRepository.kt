package com.dlab.sinsungo

class IngredientRepository {
    private val client = IngredientService.client

    suspend fun getIngredient(refID: Int) = client?.getIngredient(refID)
}
