package com.dlab.sinsungo.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dlab.sinsungo.R
import com.dlab.sinsungo.databinding.ActivityBarcodeScanBinding
import com.dlab.sinsungo.utils.BarcodeAnalyzer
import com.dlab.sinsungo.viewmodel.BarcodeScanViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarcodeScanBinding
    private val viewModel: BarcodeScanViewModel by viewModels()
    private var processingBarcode = AtomicBoolean(false)

    private lateinit var cameraExecutor: ExecutorService

    private var shortAnimationDuration: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScanBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        setPermission()

        binding.btnBack.setOnClickListener { finish() }

        cameraExecutor = Executors.newSingleThreadExecutor()

        viewModel.progressState.observe(this, {
            if (it) {
                fadeIn()
            } else {
                fadeOut()
            }
        })

        viewModel.finishFlag.observe(this, {
            if (it) {
                val intent = Intent(this@BarcodeScanActivity, BarcodeResultActivity::class.java).apply {
                    putStringArrayListExtra("barcodeScanList", ArrayList(viewModel.barcodeResult.value))
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        })
    }

    private fun setPermission() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                startCamera()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                finish()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(R.string.barcode_request_permission)
            .setRationaleConfirmText(R.string.btn_accept)
            .setDeniedMessage(R.string.ocr_denied_permission)
            .setDeniedCloseButtonText(R.string.ocr_close)
            .setPermissions(Manifest.permission.CAMERA)
            .check()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                        if (!processingBarcode.get() && barcode.isNotEmpty()) {
                            processingBarcode.set(true)
                            scanBarcode(barcode)
                        }
                    })
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {

            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun fadeIn() {
        binding.clProgress.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
    }

    private fun fadeOut() {
        binding.clProgress.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.clProgress.visibility = View.GONE
                }
            })
    }

    private fun scanBarcode(barcodes: ArrayList<String>) {
        viewModel.scanBarcode(barcodes)
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
    }
}
