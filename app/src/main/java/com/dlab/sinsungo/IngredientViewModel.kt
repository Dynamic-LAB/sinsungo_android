package com.dlab.sinsungo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IngredientViewModel(private val ingredientRepository: IngredientRepository) : ViewModel() {
    private val _ingredients = MutableLiveData<List<IngredientModel>>()
    val ingredients = _ingredients

    fun requestIngredients(refID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            ingredientRepository.getIngredient(refID)?.let { response ->
                if (response.isSuccessful) {
                    Log.d("api result", response.body().toString())
                    response.body()?.let {
                        _ingredients.postValue(it)
                    }
                }
            }
        }
    }
}
