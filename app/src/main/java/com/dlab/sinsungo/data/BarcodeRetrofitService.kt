package com.dlab.sinsungo.data

import com.dlab.sinsungo.data.model.BarcodeProduct
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BarcodeRetrofitService {
    @GET("api/{keyId}/{serviceId}/{dataType}/{startIdx}/{endIdx}/BRCD_NO={BRCD_NO}")
    suspend fun getProduct(
        @Path("keyId") keyId: String,
        @Path("serviceId") serviceId: String,
        @Path("dataType") dataType: String,
        @Path("startIdx") startIdx: String,
        @Path("endIdx") endIdx: String,
        @Path("BRCD_NO") barcode: String
    ): Response<BarcodeProduct>
}
