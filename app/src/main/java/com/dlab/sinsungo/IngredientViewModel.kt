package com.dlab.sinsungo

import android.util.Log
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
    private val _inputIngredient = MutableLiveData<IngredientModel>(IngredientModel(null, "", 0, "", "냉장", "g", "유통기한"))

    val ingredients: MutableLiveData<List<IngredientModel>> = _ingredients
    val postFlag: MutableLiveData<Boolean> = _postFlag
    val inputIngredient: MutableLiveData<IngredientModel> = _inputIngredient

    init {
        requestGetIngredients(6) // 나중에 냉장고 id 받아줘야함
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

    /*fun requestPostIngredients(ingredientModel: IngredientModel) {
        viewModelScope.launch(Dispatchers.IO) {
            IngredientRepository.postIngredient(ingredientModel).let { response ->
                if (response.isSuccessful) {
                    Log.d("post ingredient result", response.body().toString())
                    response.body()?.let {
                        withContext(Dispatchers.Main) {
                            _postFlag.postValue(true)
                            _innerList.add(it)
                            Log.d("add item", ingredientModel.toString())
                            _ingredients.postValue(_innerList)
                            Log.d("inner list", _innerList.toString())
                        }
                    }
                }
            }
        }
    }*/

    fun requestPostIngredients() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = _inputIngredient.value
            data?.id = 6 // 나중에 냉장고 id 받아줘야함
            Log.d("current inputIng", data.toString())
            IngredientRepository.postIngredient(data!!).let { response ->
                if (response.isSuccessful) {
                    Log.d("post ingredient result", response.body().toString())
                    response.body()?.let {
                        withContext(Dispatchers.Main) {
                            _postFlag.postValue(true)
                            _innerList.add(it)
                            Log.d("add item", it.toString())
                            _ingredients.postValue(_innerList)
                            Log.d("inner list", _innerList.toString())
                            _inputIngredient.postValue(IngredientModel(null, "", 0, "", "냉장", "g", "유통기한"))
                        }
                    }
                }
            }
        }
    }

    fun requestDeleteIngredient(ingredientModel: IngredientModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val ingredientID = ingredientModel.id
            IngredientRepository.deleteIngredient(ingredientID).let { response ->
                if (response.isSuccessful) {
                    Log.d("del ingredient result", response.body().toString())
                    response.body()?.let {
                        withContext(Dispatchers.Main) {
                            _innerList.remove(ingredientModel)
                            Log.d("delete item", ingredientModel.toString())
                            _ingredients.postValue(_innerList)
                            Log.d("inner list", _innerList.toString())
                        }
                    }
                }
            }
        }
    }

    fun setPostFlag(flag: Boolean) {
        _postFlag.value = flag
    }

    fun setInputModelValue(key: String, value: String) {
        val data = _inputIngredient.value
        when (key) {
            "name" -> data?.name = value
            "exdate" -> data?.exdate = value
            "refCategory" -> data?.refCategory = value
            "countType" -> data?.countType = value
            "exdateType" -> data?.exdateType = value
        }
        _inputIngredient.value = data!!
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
