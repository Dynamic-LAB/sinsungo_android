package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.data.model.Shopping
import com.dlab.sinsungo.data.repository.ShoppingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ShoppingViewModel : ViewModel() {
    private val _shoppingList = mutableListOf<Shopping>()
    private var _shoppings = MutableLiveData<List<Shopping>>()
    val shoppings: MutableLiveData<List<Shopping>> = _shoppings
    private var refId = GlobalApplication.prefs.getInt("refId")

    init {
        getShopping(refId)
    }

    fun setShopping(newShopping: Shopping?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                newShopping?.let { it ->
                    ShoppingRepository.setShopping(it).let {
                        if (it.isSuccessful) {
                            it.body()?.let { res ->
                                withContext(Dispatchers.Main) {
                                    _shoppingList.add(res)
                                    _shoppings.postValue(_shoppingList)
                                }
                            }
                        } else {
                            Log.e("shopping_error", it.message())
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }

    fun getShopping(refID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ShoppingRepository.getShopping(refID).let {
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            withContext(Dispatchers.Main) {
                                _shoppingList.addAll(res)
                                _shoppings.postValue(_shoppingList)
                            }
                        }
                    } else {
                        Log.e("shopping_error", it.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }

    fun editShopping(refID: Int, shopping: Shopping, newShopping: Shopping) {
        val pos = _shoppingList.indexOf(shopping)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ShoppingRepository.editShopping(refID, newShopping).let {
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            withContext(Dispatchers.Main) {
                                _shoppingList.removeAt(pos)
                                _shoppingList.add(pos, res)
                                _shoppings.postValue(_shoppingList)
                            }
                        }
                    } else {
                        Log.e("shopping_error", it.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }

    fun deleteShopping(shopping: Shopping) {
        viewModelScope.launch(Dispatchers.IO) {
            val shopID = shopping.id
            try {
                ShoppingRepository.delShopping(shopID).let {
                    if (it.isSuccessful) {
                        it.body()?.let { _ ->
                            withContext(Dispatchers.Main) {
                                _shoppingList.remove(shopping)
                                _shoppings.postValue(_shoppingList)
                            }
                        }
                    } else {
                        Log.e("shopping_error", it.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }

}
