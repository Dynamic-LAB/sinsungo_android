package com.dlab.sinsungo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BarcodeScanViewModel : ViewModel() {
    private val _progressState = MutableLiveData<Boolean>()
    private val _finishFlag = MutableLiveData<Boolean>()
    private val _barcodeResult = MutableLiveData<MutableList<String>>()
    private val _barcodeList = mutableListOf<String>()

    val progressState: MutableLiveData<Boolean> = _progressState
    val finishFlag: MutableLiveData<Boolean> = _finishFlag
    val barcodeResult: MutableLiveData<MutableList<String>> = _barcodeResult

    init {
        _progressState.value = false
        _finishFlag.value = false
    }

    fun scanBarcode(barcodes: ArrayList<String>) {
        _progressState.postValue(true)
        viewModelScope.launch {
            delay(1000)
            _barcodeList.addAll(barcodes)
            _barcodeResult.postValue(_barcodeList)
            _progressState.postValue(true)
            _finishFlag.postValue(true)
        }
    }
}
