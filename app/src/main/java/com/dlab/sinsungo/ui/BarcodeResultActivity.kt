package com.dlab.sinsungo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlab.sinsungo.R
import com.dlab.sinsungo.adapters.ReceiptListAdapter
import com.dlab.sinsungo.data.model.IngredientModel
import com.dlab.sinsungo.databinding.ActivityBarcodeResultBinding
import com.dlab.sinsungo.viewmodel.BarcodeResultViewModel

class BarcodeResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarcodeResultBinding
    private val viewModel: BarcodeResultViewModel by viewModels()

    private lateinit var mResultListAdapter: ReceiptListAdapter

    private val barcodeScanActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val scanResult = result.data?.getStringArrayListExtra("barcodeScanList")!!
                Log.d("scan Result", scanResult.toString())
                viewModel.searchProduct(getString(R.string.barcode_openapi_key), scanResult) {
                    Toast.makeText(this, "바코드 인식 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "취소하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeResultBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        initRcView()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnBarcodeScan.setOnClickListener {
            val barcodeScanIntent = Intent(this, BarcodeScanActivity::class.java)
            barcodeScanActivityResult.launch(barcodeScanIntent)
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
        binding.rcviewBarcodeResult.apply {
            mResultListAdapter = ReceiptListAdapter(
                { item -> deleteOCRIngredient(item) },
                { key, value, item -> setDataIngredient(key, value, item) }
            )
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mResultListAdapter
        }
    }

    private fun deleteOCRIngredient(item: IngredientModel) {
        viewModel.deleteOCRIngredient(item)
    }

    private fun setDataIngredient(key: String, value: String, item: IngredientModel) {
        viewModel.setDataIngredient(key, value, item)
    }
}
