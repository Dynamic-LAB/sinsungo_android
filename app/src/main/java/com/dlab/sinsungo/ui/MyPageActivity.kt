package com.dlab.sinsungo.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dlab.sinsungo.CustomConfirmDialog
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.MemberAdapter
import com.dlab.sinsungo.R
import com.dlab.sinsungo.databinding.ActivityMyPageBinding
import com.dlab.sinsungo.viewmodel.MyPageViewModel
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

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
        binding.btnSetNoti.isChecked = GlobalApplication.prefs.getInt("pushSetting") == 1

        binding.btnSetNoti.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.updateUser(GlobalApplication.prefs.getCurrentUser().copy(pushSetting = 1))
            } else {
                viewModel.updateUser(GlobalApplication.prefs.getCurrentUser().copy(pushSetting = 0))
            }
        }
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

    fun startNoticeActivity() {
        startActivity(Intent(this, NoticeActivity::class.java))
    }

    fun startHelpActivity() {
        startActivity(Intent(this, HelpActivity::class.java))
    }

    fun startContactActivity() {
        startActivity(Intent(this, ContactActivity::class.java))
    }

    fun startLawActivity() {
        startActivity(Intent(this, LawActivity::class.java))
    }
}
