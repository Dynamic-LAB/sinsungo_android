package com.dlab.sinsungo.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.data.model.IngredientDictModel
import com.dlab.sinsungo.data.model.IngredientModel
import com.dlab.sinsungo.data.repository.BarcodeRepository
import com.dlab.sinsungo.data.repository.IngredientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class BarcodeResultViewModel : ViewModel() {
    private val _ingredientDict = MutableLiveData<List<IngredientDictModel>>()
    private val _resultIngredients = MutableLiveData<List<IngredientModel>>()
    private val _dictList = mutableListOf<IngredientDictModel>()
    private var _resultList = mutableListOf<IngredientModel>()
    private val _postResult = MutableLiveData<Boolean>(false)

    private val serviceId = "I2570"
    private val dataType = "json"
    private val startIdx = "1"
    private val endIdx = "5"

    val resultIngredients: MutableLiveData<List<IngredientModel>> = _resultIngredients
    val postResult: MutableLiveData<Boolean> = _postResult

    private val refID = GlobalApplication.prefs.getInt("refId")

    init {
        requestGetIngredientDict()
    }

    private fun requestGetIngredientDict() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                IngredientRepository.getIngredientDict().let { response ->
                    if (response.isSuccessful) {
                        Log.d("get ing dict response", response.toString())
                        response.body()?.let {
                            withContext(Dispatchers.Main) {
                                _dictList.addAll(it)
                                _ingredientDict.postValue(_dictList)
                                Log.d("ingredient dict", it.toString())
                            }
                        }
                    } else {
                        Log.e("get ing dict fail", response.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("get ing ioexception", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    fun searchProduct(apikey: String, barcodes: ArrayList<String>, toast: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                for (barcode in barcodes) {
                    BarcodeRepository.getProduct(apikey, serviceId, dataType, startIdx, endIdx, barcode).let { response ->
                        Log.d("search product response", response.toString())
                        if (response.isSuccessful) {
                            Log.d("search product body", response.body().toString())
                            response.body()?.let {
                                withContext(Dispatchers.Main) {
                                    if (it.i2570.totalCount > 0) {
                                        var flag = false
                                        for (dict in _dictList) {
                                            for (row in it.i2570.rows) {
                                                if (row.productCategory == dict.name || row.productCategory.contains(
                                                        dict.name
                                                    )
                                                ) {
                                                    flag = true
                                                    break
                                                } else if (row.productListName == dict.name || row.productListName.contains(
                                                        dict.name
                                                    )
                                                ) {
                                                    flag = true
                                                    break
                                                } else if (row.productName == dict.name || row.productName.contains(dict.name)) {
                                                    flag = true
                                                    break
                                                }
                                            }
                                            if (flag) {
                                                _resultList.add(
                                                    IngredientModel(
                                                        refID,
                                                        dict.name,
                                                        0,
                                                        "",
                                                        "냉장",
                                                        "g",
                                                        "유통기한"
                                                    )
                                                )
                                                break
                                            }
                                        }
                                    } else {
                                        toast()
                                    }
                                }
                            }
                        }
                    }
                }
                _resultIngredients.postValue(_resultList)
            } catch (e: IOException) {
                Log.e("search product ioe", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    fun requestPostIngredient(inputValidate: (String) -> Unit) {
        var inputFlag = false

        for (it in _resultList) {
            if (it.count <= 0) {
                inputFlag = true
            }
            if (it.exDate.isEmpty() || it.exDate.isBlank()) {
                inputFlag = true
            }

            if (inputFlag) {
                inputValidate(it.name)
                break
            }
        }

        if (!inputFlag) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    IngredientRepository.postIngredient(_resultList).let { response ->
                        if (response.isSuccessful) {
                            Log.d("post ocr ing response", response.toString())
                            response.body()?.let {
                                withContext(Dispatchers.Main) {
                                    Log.d("post ocr result", it.toString())
                                    _resultList = it.toMutableList()
                                    _resultIngredients.postValue(_resultList)
                                    _postResult.postValue(true)
                                }
                            }
                        } else {
                            Log.e("post ocr ing fail", response.message())
                        }
                    }
                } catch (e: IOException) {
                    Log.e("post ing ioexception", e.message.toString())
                    e.printStackTrace()
                }
            }
        } else {
            return
        }
    }

    fun setPostResult(value: Boolean) {
        _postResult.value = value
    }

    fun addIngredientToResult(name: String) {
        _resultList.add(IngredientModel(refID, name, 0, "", "냉장", "g", "유통기한"))
        _resultIngredients.value = _resultList
    }

    fun deleteOCRIngredient(item: IngredientModel) {
        _resultList.remove(item)
        _resultIngredients.value = _resultList
    }

    fun setDataIngredient(key: String, value: String, ingredientModel: IngredientModel) {
        val idx = _resultList.indexOf(ingredientModel)
        when (key) {
            "count" -> ingredientModel.count = value.toInt()
            "exDate" -> ingredientModel.exDate = value
            "refCategory" -> ingredientModel.refCategory = value
            "countType" -> ingredientModel.countType = value
            "exDateType" -> ingredientModel.exDateType = value
        }
        _resultList[idx] = ingredientModel
        _resultIngredients.value = _resultList
    }
}
