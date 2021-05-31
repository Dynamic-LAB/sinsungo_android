package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.Event
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.data.model.User
import com.dlab.sinsungo.data.repository.UserRepository
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
    fun isRegister(userId: String, loginType: String, name: String, pushToken: String) {
        val newUser = User(userId, loginType, name, pushToken, null, 1)

        // 코루틴으로 비동기로 LoginRepository.login 호출
        viewModelScope.launch(Dispatchers.IO) {
            try {
                UserRepository.login(newUser).let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let { resUser ->
                            withContext(Dispatchers.Main) {
                                _newUser.value = resUser
                                var refId = 0
                                var token = ""

                                GlobalApplication.prefs.setString("userId", resUser.userId)
                                GlobalApplication.prefs.setString("loginType", resUser.loginType)
                                GlobalApplication.prefs.setString("name", resUser.name)
                                GlobalApplication.prefs.setInt("pushSetting", resUser.pushSetting)
                                resUser.pushToken?.let { token = it }
                                resUser.refId?.let { refId = it }
                                GlobalApplication.prefs.setString("pushToken", token)
                                GlobalApplication.prefs.setInt("refId", refId)
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
