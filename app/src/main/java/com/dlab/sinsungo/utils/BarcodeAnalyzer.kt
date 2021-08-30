package com.dlab.sinsungo.utils

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(private val barcodeListener: (barcode: ArrayList<String>) -> Unit) : ImageAnalysis.Analyzer {
    private val option = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_EAN_13
        )
        .build()
    private val scanner = BarcodeScanning.getClient(option)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val barcodeList = ArrayList<String>()
                    for (barcode in barcodes) {
                        barcodeList.add(barcode.rawValue ?: continue)
                    }
                    barcodeListener(barcodeList)
                }
                .addOnFailureListener {
                    // Do Something
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}
