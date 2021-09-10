package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.data.model.NotificationModel
import com.dlab.sinsungo.data.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class NotificationViewModel : ViewModel() {
    private val _notifications = MutableLiveData<List<NotificationModel>>()
    private val _notificationList = mutableListOf<NotificationModel>()

    val notifications = _notifications

    private val refID = GlobalApplication.prefs.getInt("refId")

    init {
        requestGetNotification(refID)
    }

    private fun requestGetNotification(refID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                NotificationRepository.getNotification(refID).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            withContext(Dispatchers.Main) {
                                _notificationList.addAll(it)
                                _notifications.postValue(_notificationList)
                            }
                        }
                    } else {
                        Log.e("get notifi not success", response.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("get notifi ioexception", e.message.toString())
                e.printStackTrace()
            }
        }
    }
}
