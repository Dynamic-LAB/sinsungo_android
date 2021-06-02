package com.dlab.sinsungo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.databinding.DataBindingUtil
import com.dlab.sinsungo.R
import com.dlab.sinsungo.databinding.ActivityNoticeDetailBinding

class NoticeDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoticeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notice_detail)

        binding.lifecycleOwner = this
        binding.view = this
        binding.notice = intent.getParcelableExtra("notice")
    }
}
