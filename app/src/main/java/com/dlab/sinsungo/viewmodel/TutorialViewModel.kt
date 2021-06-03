package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.data.model.Refrigerator
import com.dlab.sinsungo.data.repository.RefrigeratorRepository
import com.dlab.sinsungo.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class TutorialViewModel : ViewModel() {

    fun createRefrigerator(startMainActivity: () -> Unit) {
        val refrigerator = Refrigerator(GlobalApplication.prefs.getString("userId")!!, "", null)
        val body = HashMap<String, Any>()
        body["loginType"] = GlobalApplication.prefs.getString("loginType")!!
        body["refrigerator"] = refrigerator

        viewModelScope.launch(Dispatchers.IO) {
            try {
                RefrigeratorRepository.createRefrigerator(body).let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            GlobalApplication.prefs.setInt("refId", res["id"].asInt)
                            startMainActivity()
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

    fun inviteUser(inviteKey: String, startMainActivity: () -> Unit) {
        val user = GlobalApplication.prefs.getCurrentUser()
        val body = HashMap<String, Any>()
        body["inviteKey"] = inviteKey
        body["user"] = user

        viewModelScope.launch(Dispatchers.IO) {
            try {
                UserRepository.invite(body).let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            res.refId?.let {
                                GlobalApplication.prefs.setInt("refId", it)
                                startMainActivity()
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
}
