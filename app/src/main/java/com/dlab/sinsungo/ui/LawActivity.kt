package com.dlab.sinsungo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dlab.sinsungo.R
import com.dlab.sinsungo.databinding.ActivityLawBinding

class LawActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLawBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_law)

        binding.lifecycleOwner = this
        binding.view = this
    }
}
