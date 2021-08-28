package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.data.model.Recipe
import com.dlab.sinsungo.data.repository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class RecipeViewModel : ViewModel() {
    private val _normal = MutableLiveData<ArrayList<Recipe>>()
    private val _recommend = MutableLiveData<ArrayList<Recipe>>()
    private val _scrollTop = MutableLiveData<Boolean>()
    private var _trigger = false
    private var _search = MutableLiveData<Boolean>(false)

    val normal: LiveData<ArrayList<Recipe>> = _normal
    val recommend: LiveData<ArrayList<Recipe>> = _recommend
    val scrollTop: LiveData<Boolean> = _scrollTop
    val search: LiveData<Boolean> = _search
    val trigger = _trigger

    init {
        getRecipe(GlobalApplication.prefs.getInt("refId"), 0, 20)
    }

    fun getRecipe(id: Int, start: Int, end: Int, query: String = "") {
        _scrollTop.value = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (_search.value!!) {
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
                                    dataProcessing(res.normal, res.recommend)
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

    private fun dataProcessing(normal: ArrayList<Recipe>, recommend: ArrayList<Recipe>? = null) {
        var data = normal

        _normal.value?.run {
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

        _normal.postValue(data)
        recommend?.let { _recommend.postValue(it) }
    }

    fun enableSearch(id: Int, start: Int, end: Int, query: String = "") {
        _trigger = false
        _search.value = query.isNotBlank()
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

                                _normal.postValue(res)
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
