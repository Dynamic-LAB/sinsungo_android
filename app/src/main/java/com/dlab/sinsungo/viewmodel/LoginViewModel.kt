package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.Event
import com.dlab.sinsungo.data.model.User
import com.dlab.sinsungo.data.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class LoginViewModel : ViewModel() {
    private val _newUser = MutableLiveData<User>() // 로그인 한 유저
    private val _tryAuth = MutableLiveData<Event<String>>() // 로그인 시도 이벤트

    val newUser: LiveData<User> = _newUser
    val tryAuth: LiveData<Event<String>> = _tryAuth

    // 로그인 버튼 눌렸을 때
    fun onClickLogin(type: String) {
        _tryAuth.value = Event(type)
    }

    // 회원 가입 돼 있는지 확인
    fun isRegister(userId: String, loginType: String, name: String?) {
        val newUser = User(userId, loginType, name, null)

        // 코루틴으로 비동기로 LoginRepository.login 호출
        viewModelScope.launch(Dispatchers.IO) {
            try {
                LoginRepository.login(newUser).let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let { resUser ->
                            withContext(Dispatchers.Main) {
                                _newUser.value = resUser
                            }
                        }
                    } else {
                        Log.e("login_error", it.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }
}
