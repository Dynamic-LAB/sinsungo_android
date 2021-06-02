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
    private val _diets = MutableLiveData<List<Diet>>()

    private val _allIngredientList = mutableListOf<IngredientModel>()
    private val _useIngredientList = mutableListOf<IngredientModel>()
    private val _unUseIngredientList = mutableListOf<IngredientModel>()

    private var _allIngredients = MutableLiveData<List<IngredientModel>>()
    private var _useIngredients = MutableLiveData<List<IngredientModel>>()
    private var _unUseIngredients = MutableLiveData<List<IngredientModel>>()

    val diets: MutableLiveData<List<Diet>> = _diets
    val allIngredients: MutableLiveData<List<IngredientModel>> = _allIngredients
    val useIngredients: MutableLiveData<List<IngredientModel>> = _useIngredients
    val unUseIngredients: MutableLiveData<List<IngredientModel>> = _unUseIngredients

    init {
        getDiet(61)
        requestGetIngredients(61)
    }

    fun search(keyWord: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (keyWord == "") {
                    _useIngredients.postValue(_useIngredientList)
                    _unUseIngredients.postValue(_unUseIngredientList)
                } else {
                    val useSearchResult = _useIngredientList.filter { it.name.contains(keyWord) }
                    val unUseSearchResult = _unUseIngredientList.filter { it.name.contains(keyWord) }
                    _useIngredients.postValue(useSearchResult)
                    _unUseIngredients.postValue(unUseSearchResult)
                }
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
                                _allIngredientList.clear()
                                _allIngredientList.addAll(it)
                                _allIngredients.postValue(_allIngredientList)
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

    fun setDiet(newDiet: Diet?, dismiss: (() -> Unit)?) {
        Log.d("request diet data", newDiet.toString())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                newDiet?.let { it ->
                    DietRepository.setDiet(it).let {
                        if (it.isSuccessful) {
                            it.body()?.let { res ->
                                withContext(Dispatchers.Main) {
                                    Log.d("setData", res.toString())
                                    _dietList.add(res)
                                    Log.d("setData", _dietList.toString())
                                    _diets.postValue(_dietList)
                                    dismiss?.invoke()
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

    private fun getDiet(refID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                DietRepository.getDiet(refID).let {
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            withContext(Dispatchers.Main) {
                                _dietList.addAll(res)
                                Log.d("res", res.toString())
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

    fun editDiet(refId: Int, diet: Diet, newDiet: Diet, dismiss: (() -> Unit)?) {
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
                                dismiss?.invoke()
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
        _useIngredientList.clear()
        _useIngredientList.addAll(useIngredients)
        _unUseIngredientList.clear()
        _unUseIngredientList.addAll(_allIngredientList)
        _unUseIngredientList.removeAll(useIngredients)
        _useIngredients.value = _useIngredientList
        _unUseIngredients.value = _unUseIngredientList
    }

    fun clearUseIngredients() {
        _useIngredientList.clear()
        _unUseIngredientList.clear()
        _useIngredients.value = _useIngredientList
        _unUseIngredients.value = _unUseIngredientList
    }

    fun setCreate() {
        _useIngredientList.clear()
        _unUseIngredientList.clear()
        _unUseIngredientList.addAll(_allIngredientList)
        _useIngredients.value = _useIngredientList
        _unUseIngredients.value = _unUseIngredientList
    }

    fun toUseIngredient(ingredient: IngredientModel) {
        _unUseIngredientList.remove(ingredient)
        _useIngredientList.add(ingredient)
        _useIngredients.value = _useIngredientList
        _unUseIngredients.value = _unUseIngredientList
    }

    fun toUnUseIngredient(ingredient: IngredientModel) {
        _useIngredientList.remove(ingredient)
        _unUseIngredientList.add(ingredient)
        _useIngredients.value = _useIngredientList
        _unUseIngredients.value = _unUseIngredientList
    }
}
