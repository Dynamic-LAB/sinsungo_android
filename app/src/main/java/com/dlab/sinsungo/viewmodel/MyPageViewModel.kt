package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.data.model.Refrigerator
import com.dlab.sinsungo.data.model.User
import com.dlab.sinsungo.data.repository.RefrigeratorRepository
import com.dlab.sinsungo.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MyPageViewModel : ViewModel() {
    private val _refrigerator = MutableLiveData<Refrigerator>()

    val refrigerator: LiveData<Refrigerator> = _refrigerator

    init {
        getRefrigerator(GlobalApplication.prefs.getInt("refId"))
    }

    private fun getRefrigerator(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                RefrigeratorRepository.getRefrigerator(id).let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            withContext(Dispatchers.Main) {
                                res.members!!.find { it.userId == GlobalApplication.prefs.getString("userId") }?.let {
                                    val index = res.members.size / 2
                                    res.members.remove(it)
                                    res.members.add(index, it)
                                }
                                _refrigerator.postValue(res)
                            }
                        }
                    } else {
                        Log.e("refrigerator_error", it.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }

    fun updateUser(user: User, position: Int? = null, trigger: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                UserRepository.update(user).let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            withContext(Dispatchers.Main) {
                                if (res.refId == null) {
                                    position?.let {
                                        val newMembers = _refrigerator.value!!.members!!.toMutableList()
                                        newMembers.removeAt(it)
                                        val newRefrigerator =
                                            _refrigerator.value!!.copy(members = newMembers as ArrayList<User>)
                                        _refrigerator.postValue(newRefrigerator)
                                    }
                                }

                                if (trigger != null) {
                                    trigger()
                                }
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

    fun deleteUser(user: User, startLoginActivity: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                UserRepository.delete(user).let {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            startLoginActivity()
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

    fun removeMember(position: Int, dismiss: () -> Unit) {
        val user = _refrigerator.value!!.members!![position]
        var pushToken = ""
        user.pushToken?.let { pushToken = it }
        val deleteUser = User(user.userId, user.loginType, user.name, pushToken, 0, user.pushSetting)
        updateUser(deleteUser, position, dismiss)
    }
}
