package com.dlab.sinsungo.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BarcodeProduct(
    @SerializedName("I2570")
    val i2570: BarcodeResponse
) : Parcelable

@Parcelize
data class BarcodeResponse (
    @SerializedName("RESULT")
    val result: BarcodeResult,

    @SerializedName("total_count")
    val totalCount: Int,

    @SerializedName("row")
    val rows: List<BarcodeRow>
) : Parcelable

@Parcelize
data class BarcodeResult (
    @SerializedName("MSG")
    val message: String,

    @SerializedName("CODE")
    val code: String
) : Parcelable

@Parcelize
data class BarcodeRow (
    @SerializedName("CMPNY_NM")
    val companyName: String,

    @SerializedName("HTRK_PRDLST_NM")
    val productType: String,

    @SerializedName("PRDT_NM")
    val productName: String,

    @SerializedName("BRCD_NO")
    val barcodeNumber: String,

    @SerializedName("PRDLST_NM")
    val productCategory: String,

    @SerializedName("LAST_UPDT_DTM")
    val updateDate: String,

    @SerializedName("HRNK_PRDLST_NM")
    val productListName: String,

    @SerializedName("PRDLST_REPORT_NO")
    val reportNumber: String
) : Parcelable
