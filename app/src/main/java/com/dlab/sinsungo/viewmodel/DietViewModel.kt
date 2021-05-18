package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.data.model.Diet
import com.dlab.sinsungo.data.repository.DietRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class DietViewModel : ViewModel() {
    private val _dietList = mutableListOf<Diet>()
    private var _diets = MutableLiveData<List<Diet>>()
    val diets: MutableLiveData<List<Diet>> = _diets

    init {
        getDiet(5)
    }

    fun setDiet(newDiet: Diet?) {
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

}
