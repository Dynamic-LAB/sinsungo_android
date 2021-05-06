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
    private val _shoppings = MutableLiveData<Shopping>()
    val shoppings = _shoppings

    fun requestShopping(newShopping: Shopping) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ShoppingRepository.setShopping(newShopping).let {
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            withContext(Dispatchers.Main) {
                                _shoppings.postValue(res)
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
