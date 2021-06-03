package com.dlab.sinsungo.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlab.sinsungo.adapters.NoticeAdapter
import com.dlab.sinsungo.R
import com.dlab.sinsungo.data.model.Notice
import com.dlab.sinsungo.databinding.ActivityNoticeBinding
import com.dlab.sinsungo.viewmodel.NoticeViewModel

class NoticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoticeBinding
    private val viewModel: NoticeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        setRecyclerView()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notice)

        binding.lifecycleOwner = this
        binding.view = this
        binding.viewModel = viewModel
    }

    private fun setRecyclerView() {
        binding.rvNotice.apply {
            adapter = NoticeAdapter { notice -> moveToDetail(notice) }
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun moveToDetail(notice: Notice) {
        val intent = Intent(this, NoticeDetailActivity::class.java)
        intent.putExtra("notice", notice)
        startActivity(intent)
    }
}
