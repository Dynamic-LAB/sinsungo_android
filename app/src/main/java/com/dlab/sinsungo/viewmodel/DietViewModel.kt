package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.IngredientModel
import com.dlab.sinsungo.IngredientRepository
import com.dlab.sinsungo.data.model.Diet
import com.dlab.sinsungo.data.repository.DietRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class DietViewModel : ViewModel() {
    private val _dietList = mutableListOf<Diet>()
    private val _ingredientList = mutableListOf<IngredientModel>()
    private val _allIngredientList = mutableListOf<IngredientModel>()
    private val _useIngredientList = mutableListOf<IngredientModel>()
    private var _diets = MutableLiveData<List<Diet>>()
    private var _ingredients = MutableLiveData<List<IngredientModel>>()
    private var _useIngredients = MutableLiveData<List<IngredientModel>>()
    val diets: MutableLiveData<List<Diet>> = _diets
    val ingredients: MutableLiveData<List<IngredientModel>> = _ingredients
    val useIngredients = _useIngredients

    init {
        getDiet(5)
        requestGetIngredients(5)
    }

    fun search(keyWord: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val searchResult = _ingredientList.distinct().filter { it.name.contains(keyWord) }
                Log.d("searchResult", searchResult.toString())
                _ingredients.postValue(searchResult)
                Log.d("ingredients", _ingredients.toString())
                Log.d("_allIngredientList", _allIngredientList.toString())
            } catch (e: IOException) {
                Log.e("get ing ioexception", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    // all ref data
    fun requestGetIngredients(refID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                IngredientRepository.getIngredient(refID).let { response ->
                    if (response.isSuccessful) {
                        Log.d("get ing response", response.toString())
                        response.body()?.let {
                            withContext(Dispatchers.Main) {
                                _ingredientList.clear()
                                _ingredientList.addAll(it)
                                _ingredients.postValue(_ingredientList)
                            }
                        }
                    } else {
                        Log.e("get ing not success", response.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("get ing ioexception", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    fun setDiet(newDiet: Diet?) {
        Log.d("request diet data", newDiet.toString())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                newDiet?.let { it ->
                    DietRepository.setDiet(it).let {
                        if (it.isSuccessful) {
                            it.body()?.let { res ->
                                withContext(Dispatchers.Main) {
                                    _dietList.add(res)
                                    _diets.postValue(_dietList)
                                }
                            }
                        } else {
                            Log.e("diet_error", it.message())
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }

    fun getDiet(refID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                DietRepository.getDiet(refID).let {
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            withContext(Dispatchers.Main) {
                                _dietList.addAll(res)
                                _diets.postValue(_dietList)
                            }
                        }
                    } else {
                        Log.e("diet_error", it.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }

    fun editDiet(refId: Int, diet: Diet, newDiet: Diet) {
        Log.d("보낸 data", diet.toString() + newDiet.toString())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                DietRepository.editDiet(refId, listOf(diet, newDiet)).let {
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            withContext(Dispatchers.Main) {
                                val pos = _dietList.indexOf(diet)
                                _dietList.removeAt(pos)
                                _dietList.add(pos, res)
                                _diets.postValue(_dietList)
                                Log.d("edit data", res.toString())
                            }
                        }
                    } else {
                        Log.e("diet_error", it.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }

    fun deleteDiet(diet: Diet) {
        viewModelScope.launch(Dispatchers.IO) {
            val dietId = diet.id
            try {
                DietRepository.delDiet(dietId).let {
                    if (it.isSuccessful) {
                        it.body()?.let { _ ->
                            withContext(Dispatchers.Main) {
                                _dietList.remove(diet)
                                _diets.postValue(_dietList)
                            }
                        }
                    } else {
                        Log.e("diet_error", it.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }

    fun setUseIngredients(useIngredients: List<IngredientModel>) {
        _useIngredientList.addAll(useIngredients)
        _useIngredients.value = _useIngredientList
    }

    fun clearUseIngredients() {
        _useIngredientList.clear()
        _useIngredients.value = _useIngredientList
    }
}
