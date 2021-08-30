package com.dlab.sinsungo.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dlab.sinsungo.adapters.ReceiptListAdapter
import com.dlab.sinsungo.data.model.IngredientModel
import com.dlab.sinsungo.databinding.ActivityReceiptOcrBinding
import com.dlab.sinsungo.databinding.DialogReceiptAddIngredientBinding
import com.dlab.sinsungo.viewmodel.ReceiptOCRViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions

class ReceiptOCRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReceiptOcrBinding
    private val viewModel: ReceiptOCRViewModel by viewModels()

    private val takePictureActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                ocrUri = result.data?.getParcelableExtra("ocr uri")
                if (ocrUri == null) {
                    Toast.makeText(this, "영수증을 먼저 촬영해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    binding.ivOcrPicture.setImageURI(ocrUri)
                }
            } else {
                Toast.makeText(this, "취소하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    private val getPictureFromGalleryActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                ocrUri = result.data?.data
                if (ocrUri == null) {
                    Toast.makeText(this, "영수증 사진을 선택해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    Glide.with(this).load(ocrUri).into(binding.ivOcrPicture)
                }
            } else {
                Toast.makeText(this, "취소하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    private val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
    private var ocrUri: Uri? = null

    private lateinit var mReceiptListAdapter: ReceiptListAdapter

    private var shortAnimationDuration: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptOcrBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        initRcView()

        shortAnimationDuration = resources.getInteger(android.R.integer.config_longAnimTime)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnOpenCamera.setOnClickListener {
            val takePhoto = Intent(this, CameraXOCRActivity::class.java)
            takePictureActivityResult.launch(takePhoto)
        }

        binding.btnOpenGallery.setOnClickListener {
            val getPhoto = Intent(Intent.ACTION_GET_CONTENT)
            getPhoto.type = MediaStore.Images.Media.CONTENT_TYPE
            getPhoto.type = "image/*"
            getPictureFromGalleryActivityResult.launch(getPhoto)
        }

        binding.btnOcr.setOnClickListener {
            if (ocrUri == null) {
                Toast.makeText(this, "영수증 사진이 존재하지 않습니다!", Toast.LENGTH_SHORT).show()
            } else {
                fadeIn()
                val image = InputImage.fromFilePath(this, ocrUri)
                recognizer.process(image)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("MLKIT", "성공")
                            val visionText = it.result.text
                            Log.d("MLKIT RESULT", visionText)
                            viewModel.extractIngredientInOCR(visionText)
                        } else {
                            Log.d("MLKIT", "실패")
                            Log.e("MLKIT", it.exception.message.toString())
                        }
                        fadeOut()
                    }
            }
        }

        binding.btnSave.setOnClickListener {
            viewModel.requestPostIngredient { ingredientName ->
                AlertDialog.Builder(this)
                    .setMessage("${ingredientName}에 입력하지 않은 값이 있습니다.")
                    .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }

        binding.btnAdd.setOnClickListener {
            val dialogView = DialogReceiptAddIngredientBinding.inflate(layoutInflater)

            AlertDialog.Builder(this)
                .setView(dialogView.root)
                .setNegativeButton("취소") { dialog, _ -> dialog?.dismiss() }
                .setPositiveButton("추가") { dialog, _ ->
                    val name = dialogView.etIngredientName.text.toString()
                    viewModel.addIngredientToResult(name)
                    dialog?.dismiss()
                }
                .create()
                .show()
        }

        viewModel.postResult.observe(this) {
            if (it == true) {
                val ocrPostResult = Intent(this, RefrigeratorFragment::class.java).apply {
                    putParcelableArrayListExtra("ocrPostResult", ArrayList(viewModel.resultIngredients.value))
                }
                setResult(RESULT_OK, ocrPostResult)
                viewModel.setPostResult(false)
                if (!isFinishing) finish()
            }
        }
    }

    private fun initRcView() {
        binding.rcviewOcrResult.apply {
            mReceiptListAdapter = ReceiptListAdapter(
                { item -> deleteOCRIngredient(item) },
                { key, value, item -> setDataIngredient(key, value, item) }
            )
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mReceiptListAdapter
        }
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

    private fun deleteOCRIngredient(item: IngredientModel) {
        viewModel.deleteOCRIngredient(item)
    }

    private fun setDataIngredient(key: String, value: String, item: IngredientModel) {
        viewModel.setDataIngredient(key, value, item)
    }
}
