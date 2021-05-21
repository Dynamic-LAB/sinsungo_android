package com.dlab.sinsungo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dlab.sinsungo.databinding.ActivityReceiptOcrBinding

class ReceiptOCRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReceiptOcrBinding

    private lateinit var getActivityResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptOcrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val ocrUri = result.data?.getParcelableExtra<Uri>("ocr uri")
                Log.d("Picture Result", "$ocrUri")
                Glide.with(this).load(ocrUri).into(binding.ivOcrPicture)
            }
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnOpenCamera.setOnClickListener {
            val getOCRPhoto = Intent(this, CameraXOCRActivity::class.java)
            getActivityResult.launch(getOCRPhoto)
        }
    }
}
