package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.data.model.IngredientModel
import com.dlab.sinsungo.data.repository.IngredientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class IngredientViewModel : ViewModel() {
    private val _ingredients = MutableLiveData<List<IngredientModel>>()
    private var _innerList = mutableListOf<IngredientModel>()
    private val _dialogDismissFlag = MutableLiveData<Boolean>()
    private val _inputIngredient =
        MutableLiveData<IngredientModel>()
    private val _isModify = MutableLiveData<Boolean>()
    private val _position = MutableLiveData<Int>()

    val ingredients: MutableLiveData<List<IngredientModel>> = _ingredients
    val dialogDismissFlag: MutableLiveData<Boolean> = _dialogDismissFlag
    val inputIngredient: MutableLiveData<IngredientModel> = _inputIngredient
    val isModify: MutableLiveData<Boolean> = _isModify

    private val refID = GlobalApplication.prefs.getInt("refId")

    init {
        requestGetIngredients(refID) // 나중에 냉장고 id 받아줘야함
        _inputIngredient.value = IngredientModel(null, "", 0, "", "냉장", "g", "유통기한")
    }

    // select all
    fun requestGetIngredients(refID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                IngredientRepository.getIngredient(refID).let { response ->
                    if (response.isSuccessful) {
                        Log.d("get ing response", response.toString())
                        response.body()?.let {
                            withContext(Dispatchers.Main) {
                                _innerList = it.toMutableList()
                                _ingredients.postValue(_innerList)
                                Log.d("ingredient list", it.toString())
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

    // 추가
    fun requestPostIngredient() {
        viewModelScope.launch(Dispatchers.IO) {
            val inputList = mutableListOf<IngredientModel>()
            val data = _inputIngredient.value
            data?.id = refID
            Log.d("input ing", data.toString())
            inputList.add(data!!)
            try {
                IngredientRepository.postIngredient(inputList).let { response ->
                    if (response.isSuccessful) {
                        Log.d("post ing response", response.toString())
                        response.body()?.let {
                            withContext(Dispatchers.Main) {
                                _innerList.addAll(it)
                                _ingredients.postValue(_innerList)
                                Log.d("add ing item", it.toString())
                                _inputIngredient.postValue(IngredientModel(null, "", 0, "", "냉장", "g", "유통기한"))
                                _dialogDismissFlag.postValue(true)
                            }
                        }
                    } else {
                        Log.e("post ing not success", response.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("post ing ioexception", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    // 장바구니에서 추가
    fun requestPostIngredient(inputList: MutableList<IngredientModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                IngredientRepository.postIngredient(inputList).let { response ->
                    if (response.isSuccessful) {
                        Log.d("post ing response", response.toString())
                        response.body()?.let {
                            withContext(Dispatchers.Main) {
                                _innerList.addAll(it)
                                _ingredients.postValue(_innerList)
                                Log.d("add ing item", it.toString())
                            }
                        }
                    } else {
                        Log.e("post ing not success", response.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("post ing ioexception", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    // 수정
    fun requestPutIngredient() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = _inputIngredient.value
            val position = _position.value
            Log.d("modify input ing", data.toString())
            try {
                IngredientRepository.putIngredient(refID, data!!).let { response ->
                    if (response.isSuccessful) {
                        Log.d("put ing response", response.toString())
                        response.body()?.let {
                            withContext(Dispatchers.Main) {
                                Log.d("update ing item", it.toString())
                                _innerList[position!!] = it
                                _ingredients.postValue(_innerList)
                                _inputIngredient.postValue(IngredientModel(null, "", 0, "", "냉장", "g", "유통기한"))
                                _dialogDismissFlag.postValue(true)
                            }
                        }
                    } else {
                        Log.e("put ing not success", response.message())
                    }
                }

            } catch (e: IOException) {
                Log.e("put ing ioexception", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    // 삭제
    fun requestDeleteIngredient(ingredientModel: IngredientModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val ingredientID = ingredientModel.id
            Log.d("delete item", ingredientModel.toString())
            try {
                IngredientRepository.deleteIngredient(ingredientID).let { response ->
                    if (response.isSuccessful) {
                        Log.d("delete ing response", response.toString())
                        response.body()?.let {
                            withContext(Dispatchers.Main) {
                                _innerList.remove(ingredientModel)
                                _ingredients.postValue(_innerList)
                            }
                        }
                    } else {
                        Log.e("delete ing not success", response.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("delete ing ioexception", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    // 다이얼로그 확인 버튼
    fun inputOnClick() {
        when (_isModify.value) {
            true -> requestPutIngredient() // 수정
            false -> requestPostIngredient() // 추가
        }
    }

    // 다이얼로그 취소 버튼
    fun dismissOnClick() {
        setInputIngredient(IngredientModel(null, "", 0, "", "냉장", "g", "유통기한"))
        setDialogDismissFlag(true)
    }

    fun setDialogDismissFlag(flag: Boolean) {
        _dialogDismissFlag.value = flag
    }

    // 수정 때만 호출
    fun setInputIngredient(ingredientModel: IngredientModel) {
        _inputIngredient.value = ingredientModel.copy()
        _position.value = _innerList.indexOf(ingredientModel)
    }

    fun setModify(flag: Boolean) {
        _isModify.value = flag
    }

    fun setInputIngredientValue(key: String, value: String) {
        val data = _inputIngredient.value
        when (key) {
            "exDate" -> data?.exdate = value
            "refCategory" -> data?.refCategory = value
            "countType" -> data?.countType = value
            "exDateType" -> data?.exDateType = value
        }
        _inputIngredient.value = data!!
    }

    fun addOcrResult(list: ArrayList<IngredientModel>) {
        _innerList.addAll(list.toMutableList())
        _ingredients.value = _innerList
    }
}
