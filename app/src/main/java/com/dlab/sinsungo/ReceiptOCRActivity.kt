package com.dlab.sinsungo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlab.sinsungo.databinding.ActivityReceiptOcrBinding
import com.dlab.sinsungo.databinding.DialogReceiptAddIngredientBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.ktx.Firebase
import com.google.gson.*
import java.io.ByteArrayOutputStream

class ReceiptOCRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReceiptOcrBinding
    private val viewModel: ReceiptOCRViewModel by viewModels()

    private lateinit var getActivityResult: ActivityResultLauncher<Intent>

    private var ocrUri: Uri? = null
    private lateinit var bitmap: Bitmap

    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private val EMAIL = "lksy1294@gmail.com"
    private val PASS_WORD = "asdf1111"

    private lateinit var mReceiptListAdapter: ReceiptListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptOcrBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        functions = FirebaseFunctions.getInstance()
        auth = Firebase.auth

        getActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                ocrUri = result.data?.getParcelableExtra<Uri>("ocr uri")
                Log.d("Picture Result", "$ocrUri")
                if (ocrUri == null) {
                    Toast.makeText(this, "영수증을 먼저 촬영해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    bitmap = getBitmap(ocrUri!!)
                    bitmap = scaleBitmapDown(bitmap, 640)
                    binding.ivOcrPicture.setImageBitmap(bitmap)
                }
            }
        }

        initRcView()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSignIn.setOnClickListener { signIn(EMAIL, PASS_WORD) }
        binding.btnCreateAccount.setOnClickListener { createAccount(EMAIL, PASS_WORD) }
        binding.btnReload.setOnClickListener { reload() }

        binding.btnOpenCamera.setOnClickListener {
            val getOCRPhoto = Intent(this, CameraXOCRActivity::class.java)
            getActivityResult.launch(getOCRPhoto)
        }

        binding.btnOcr.setOnClickListener {
            binding.clProgress.visibility = View.VISIBLE
            requestOCR()
        }

        binding.btnSave.setOnClickListener {
            Log.d("save ing list", mReceiptListAdapter.currentList.toString())
            viewModel.requestPostIngredient()
        }

        binding.btnAdd.setOnClickListener {
            val dialogView = DialogReceiptAddIngredientBinding.inflate(layoutInflater)

            AlertDialog.Builder(this)
                .setView(dialogView.root)
                .setNegativeButton("취소") { dialog, which -> dialog?.dismiss() }
                .setPositiveButton("추가") { dialog, which ->
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

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun initRcView() {
        binding.rcviewOcrResult.apply {
            mReceiptListAdapter = ReceiptListAdapter { position -> deleteOCRIngredient(position) }
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mReceiptListAdapter
        }
    }

    private fun deleteOCRIngredient(position: Int) {
        viewModel.deleteOCRIngredient(position)
    }

    private fun getBitmap(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val decode = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(decode)
        }
    }

    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var resizedWidth = maxDimension
        var resizedHeight = maxDimension
        when {
            originalHeight > originalWidth -> {
                resizedHeight = maxDimension
                resizedWidth =
                    (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
            }
            originalWidth > originalHeight -> {
                resizedWidth = maxDimension
                resizedHeight =
                    (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
            }
            originalHeight == originalWidth -> {
                resizedHeight = maxDimension
                resizedWidth = maxDimension
            }
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false)
    }

    private fun requestOCR() {
        // Convert bitmap to base64 encoded string
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        val base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

        // Create json request to cloud vision
        val request = JsonObject()
        // Add image to request
        val image = JsonObject()
        image.add("content", JsonPrimitive(base64encoded))
        request.add("image", image)

        //Add features to the request
        val feature = JsonObject()
        feature.add("type", JsonPrimitive("TEXT_DETECTION"))
        // feature.add("type", JsonPrimitive("DOCUMENT_TEXT_DETECTION"))

        val features = JsonArray()
        features.add(feature)
        request.add("features", features)

        Log.d("ocr request", request.toString())

        annotateImage(request.toString())
            .addOnCompleteListener { task ->
                Log.d("task", task.result.toString())
                if (task.isSuccessful) {
                    Log.d("ocr", "성공")
                    val annotation = task.result!!.asJsonArray[0].asJsonObject["fullTextAnnotation"].asJsonObject
//                    System.out.format("%nComplete annotation:")
//                    System.out.format("%n%s", annotation["text"].asString)
                    val ocrResult: String = annotation["text"].asString
                    Log.d("ocr result", ocrResult)
                    viewModel.extractIngredientInOCR(ocrResult)
                    binding.btnAdd.visibility = View.VISIBLE
                } else {
                    Log.d("ocr", "실패")
                    val e = task.exception
                    if (e is FirebaseFunctionsException) {
                        Log.d("error", e.code.toString())
                    }
                }
                binding.clProgress.visibility = View.GONE
            }
    }

    private fun annotateImage(requestJson: String): Task<JsonElement> {
        return functions
            .getHttpsCallable("annotateImage")
            .call(requestJson)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data
                JsonParser.parseString(Gson().toJson(result))
            }
    }

    private fun createAccount(email: String, password: String) {
        Log.d("firebase auth", "createAccount:$email")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("firebase auth", "createAccount success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.d("firebase auth", "createAccount fail")
                }
            }
    }

    private fun signIn(email: String, password: String) {
        Log.d("firebase auth", "signIn:$email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("firebase auth", "signIn success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.d("firebase auth", "signIn fail")
                }
            }
    }

    private fun reload() {
        auth.currentUser!!.reload().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUI(auth.currentUser)
            } else {
                Log.d("firebase auth", "reload fail")
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Log.d("user", user.toString())
        } else {
            Log.d("user", "is null")
        }
    }
}
