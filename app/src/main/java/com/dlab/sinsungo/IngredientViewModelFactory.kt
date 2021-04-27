package com.dlab.sinsungo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class IngredientViewModelFactory(private val ingredientRepository: IngredientRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IngredientRepository::class.java).newInstance(ingredientRepository)
    }
}
