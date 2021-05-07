package com.dlab.sinsungo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class IngredientViewModel(private val ingredientRepository: IngredientRepository) : ViewModel() {
    private val _ingredients = MutableLiveData<List<IngredientModel>>()
    val ingredients = _ingredients

    fun requestIngredients(refID: Int, key: String) {
        CoroutineScope(Dispatchers.IO).launch {
            ingredientRepository.getIngredient(refID)?.let { response ->
                if (response.isSuccessful) {
                    Log.d("api result", response.body().toString())
                    response.body()?.let {
                        withContext(Dispatchers.Main) {
                            filterList(it, key)
                        }
                    }
                }
            }
        }
    }

    private fun filterList(list: List<IngredientModel>, key: String) {
        var filteredList = list

        filteredList = filteredList.filter { it.refCategory == key }

        _ingredients.postValue(filteredList)
    }

    fun sortList(key: String) {
        CoroutineScope(Dispatchers.Main).launch {
            var data = _ingredients.value

            when (key) {
                "재료명 순" -> data = data!!.sortedBy { it.name }
                "신선도 순" -> {
                    val exDateList = data!!.filter { it.exdateType == "유통기한" }
                    val notExDateList = data.filter { it.exdateType != "유통기한" }
                    data = exDateList.sortedBy { it.exdate } + notExDateList.sortedBy { it.exdate }
                }
                "최근 추가 순" -> data = data!!.sortedBy { it.id }
            }

            _ingredients.postValue(data!!)
        }
    }
}
