package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.data.model.User
import com.dlab.sinsungo.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class SplashViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun getCurrentUser() {
        val user = User(
            GlobalApplication.prefs.getString("userId")!!,
            GlobalApplication.prefs.getString("loginType")!!,
            GlobalApplication.prefs.getString("name")!!,
            GlobalApplication.prefs.getString("pushToken"),
            GlobalApplication.prefs.getInt("refId"),
            GlobalApplication.prefs.getInt("pushSetting")
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                UserRepository.getUser(user).let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            withContext(Dispatchers.Main) {
                                _user.postValue(res)
                                var refId = 0

                                res.refId?.let { refId = it }
                                GlobalApplication.prefs.setInt("refId", refId)
                                GlobalApplication.prefs.setInt("pushSetting", res.pushSetting)
                            }
                        }
                    } else {
                        Log.e("user_error", it.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }
}
