package com.dlab.sinsungo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.dlab.sinsungo.CustomConfirmDialog
import com.dlab.sinsungo.MemberAdapter
import com.dlab.sinsungo.R
import com.dlab.sinsungo.databinding.ActivityMyPageBinding
import com.dlab.sinsungo.viewmodel.MyPageViewModel
import com.google.android.flexbox.*

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPageBinding
    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        setRecyclerView()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page)

        binding.lifecycleOwner = this
        binding.view = this
        binding.viewModel = viewModel
    }

    private fun setRecyclerView() {
        binding.rvMember.apply {
            adapter = MemberAdapter { type -> showDialog(type) }
            layoutManager = FlexboxLayoutManager(this.context).apply {
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.CENTER
            }
            setHasFixedSize(true)
        }
    }

    fun showDialog(type: String) {
        val dialog = CustomConfirmDialog(type)
        dialog.show(supportFragmentManager, "custom_confirm_dialog")
    }
}
