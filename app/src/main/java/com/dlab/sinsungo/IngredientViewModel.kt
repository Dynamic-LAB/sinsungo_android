package com.dlab.sinsungo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngredientViewModel : ViewModel() {
    private val _ingredients = MutableLiveData<List<IngredientModel>>()
    private val _innerList = mutableListOf<IngredientModel>()
    private val _postFlag = MutableLiveData<Boolean>()

    val ingredients: MutableLiveData<List<IngredientModel>> = _ingredients
    val postFlag: LiveData<Boolean> = _postFlag

    init {
        requestGetIngredients(6)
    }

    fun requestGetIngredients(refID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            IngredientRepository.getIngredient(refID).let { response ->
                if (response.isSuccessful) {
                    Log.d("get ingredient result", response.body().toString())
                    response.body()?.let {
                        withContext(Dispatchers.Main) {
                            _innerList.addAll(it)
                            _ingredients.postValue(_innerList)
                        }
                    }
                }
            }
        }
    }

    fun requestPostIngredients(ingredientModel: IngredientModel) {
        viewModelScope.launch(Dispatchers.IO) {
            IngredientRepository.postIngredient(ingredientModel).let { response ->
                if (response.isSuccessful) {
                    Log.d("post ingredient result", response.body().toString())
                    response.body()?.let {
                        withContext(Dispatchers.Main) {
                            _postFlag.postValue(true)
                            _innerList.add(ingredientModel)
                            Log.d("add item", ingredientModel.toString())
                            _ingredients.postValue(_innerList)
                            Log.d("inner list", _innerList.toString())

                        }
                    }
                }
            }
        }
    }

    /*fun sortList(key: String) {
        viewModelScope.launch(Dispatchers.Main) {
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
    }*/
}
