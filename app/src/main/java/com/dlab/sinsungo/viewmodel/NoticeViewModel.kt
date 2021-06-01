package com.dlab.sinsungo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dlab.sinsungo.data.model.Notice
import com.dlab.sinsungo.data.repository.NoticeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class NoticeViewModel : ViewModel() {
    private val _notice = MutableLiveData<List<Notice>>()

    val notice: LiveData<List<Notice>> = _notice

    init {
        getNotice()
    }

    private fun getNotice() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                NoticeRepository.getNotice().let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let { res ->
                            withContext(Dispatchers.Main) {
                                _notice.postValue(res)
                            }
                        }
                    } else {
                        Log.e("notice_error", it.message())
                    }
                }
            } catch (e: IOException) {
                Log.e("io_error", e.message!!)
                e.printStackTrace()
            }
        }
    }
}
