package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.data.model.Shopping
import com.dlab.sinsungo.data.repository.ShoppingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ShoppingViewModel : ViewModel() {
    private val shoppingList = mutableListOf<Shopping>()
    private var _shoppings = MutableLiveData<List<Shopping>>()
    val shoppings: MutableLiveData<List<Shopping>> = _shoppings
    var visible = false

    init {
        getShopping(5)
    }

    fun setShopping(newShopping: Shopping?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                newShopping?.let {
                    ShoppingRepository.setShopping(it).let {
                        if (it.isSuccessful) {
                            it.body()?.let { res ->
                                withContext(Dispatchers.Main) {
                                    Log.d("set res result", res.toString())
                                    Log.d("set data", newShopping.toString())
                                    shoppingList.add(res)
                                    _shoppings.postValue(shoppingList)
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
                                shoppingList.addAll(res)
                                checkNull(res)
                                _shoppings.postValue(shoppingList)
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

    fun deleteShopping(shopping: Shopping?){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (shopping != null) {
                    shopping.id?.let { it ->
                        ShoppingRepository.delShopping(it).let {
                            if (it.isSuccessful) {
                                it.body()?.let { res ->
                                    withContext(Dispatchers.Main) {
                                        Log.d("shoppingList",shoppingList.size.toString())
                                        shoppingList.remove(shopping)
                                        checkNull(shoppingList)
                                        _shoppings.postValue(shoppingList)
                                        Log.d("postValue ", res.toString())
                                        Log.d("shoppingList",shoppingList.size.toString())
                                    }
                                }
                            } else {
                                Log.e("shopping_error", it.message())
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }



    private fun checkNull(item: List<Shopping>) {
        visible = item.isEmpty()
    }
}
