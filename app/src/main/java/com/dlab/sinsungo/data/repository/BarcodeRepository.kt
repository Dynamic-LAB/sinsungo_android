package com.dlab.sinsungo.data.repository

import com.dlab.sinsungo.data.RemoteDataSource

object BarcodeRepository {
    private val service = RemoteDataSource.barcodeService
    suspend fun getProduct(
        keyId: String,
        serviceId: String,
        dataType: String,
        startIdx: String,
        endIdx: String,
        barcode: String) = service.getProduct(keyId, serviceId, dataType, startIdx, endIdx, barcode)
}
