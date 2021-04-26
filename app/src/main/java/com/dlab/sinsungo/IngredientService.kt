package com.dlab.sinsungo

object IngredientService {
    private const val BASE_URL = "http://158.247.218.140:5001"

    val client = BaseService().getClient(BASE_URL)?.create(IngredientApi::class.java)
}
