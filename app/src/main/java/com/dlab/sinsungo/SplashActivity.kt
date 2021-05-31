package com.dlab.sinsungo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dlab.sinsungo.databinding.ActivitySplashBinding
import com.dlab.sinsungo.ui.LoginActivity
import com.dlab.sinsungo.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()

        // 로그인 돼 있을 때
        if (GlobalApplication.prefs.getString("userId") != "") {
            viewModel.getCurrentUser()
        } else { // 로그인 안 돼 있을 때
            Glide.with(this).load(R.raw.logo).into(binding.ivLoading)
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, 4500)
        }
    }

    private fun init() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        viewModel.user.observe(this) {
            val intent: Intent by lazy {
                // 냉장고 있을 때
                if (it.refId != null) Intent(this, MainActivity::class.java)
                // 냉장고 없을 때
                else Intent(this, TutorialActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}
