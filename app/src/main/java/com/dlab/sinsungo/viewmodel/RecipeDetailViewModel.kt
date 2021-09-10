package com.dlab.sinsungo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dlab.sinsungo.data.model.Recipe

class RecipeDetailViewModel : ViewModel() {
    private val _recipe = MutableLiveData<Recipe>()

    val recipe: LiveData<Recipe> = _recipe

    fun setRecipe(recipe: Recipe) {
        _recipe.value = recipe
    }

    fun inRefIngredients(): String {
        return _recipe.value!!.inRefIngredients.distinct().joinToString(", ")
    }

    fun notInRefIngredients(): String {
        return _recipe.value!!.notInRefIngredients.joinToString(", ")
    }
}
