package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.data.model.Recipe
import com.dlab.sinsungo.data.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class RecipeViewModel : ViewModel() {
    private val _recipes = MutableLiveData<ArrayList<Recipe>>()
    private val _scrollTop = MutableLiveData<Boolean>()
    private var _trigger = false
    private var _search = false

    val recipes: LiveData<ArrayList<Recipe>> = _recipes
    val scrollTop: LiveData<Boolean> = _scrollTop
    val trigger = _trigger

    init {
        getRecipe(5, 0, 20)
    }

    fun getRecipe(id: Int, start: Int, end: Int, query: String = "") {
        _scrollTop.value = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (_search) {
                    RecipeRepository.searchRecipe(id, start, end, query).let { it ->
                        if (it.isSuccessful) {
                            it.body()?.let { res ->
                                withContext(Dispatchers.Main) {
                                    dataProcessing(res)
                                }
                            }
                        } else {
                            Log.e("recipe_error", it.message())
                        }
                    }
                } else {
                    RecipeRepository.getRecipe(id, start, end).let { it ->
                        if (it.isSuccessful) {
                            it.body()?.let { res ->
                                withContext(Dispatchers.Main) {
                                    dataProcessing(res)
                                }
                            }
                        } else {
                            Log.e("recipe_error", it.message())
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }

    private fun dataProcessing(res: ArrayList<Recipe>) {
        var data = res

        _recipes.value?.run {
            val oldData = this.toMutableList()
            oldData.removeLast()

            if (oldData.slice(this.lastIndex - 20 until this.lastIndex - 1)
                    .containsAll(data)
            ) {
                _trigger = true
                data = oldData as ArrayList<Recipe>
            } else {
                data = (oldData + data) as ArrayList<Recipe>
            }
        }

        if (!_trigger) {
            data.add(
                Recipe(
                    0, "", "", "", "", arrayListOf(),
                    arrayListOf()
                )
            )
        }

        _recipes.postValue(data)
    }

    fun enableSearch(id: Int, start: Int, end: Int, query: String = "") {
        _trigger = false
        _search = true
        _scrollTop.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                RecipeRepository.searchRecipe(id, start, end, query).let {
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            withContext(Dispatchers.Main) {
                                if (res.size > 19) {
                                    res.add(
                                        Recipe(
                                            0, "", "", "", "", arrayListOf(),
                                            arrayListOf()
                                        )
                                    )
                                }

                                _recipes.postValue(res)
                            }
                        }
                    } else {
                        Log.e("recipe_error", it.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }
}
