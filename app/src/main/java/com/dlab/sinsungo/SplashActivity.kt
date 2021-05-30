package com.dlab.sinsungo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dlab.sinsungo.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: 로그인 되어 있는 상태일 경우
        // 튜토리얼 본 거 체크
        val isTutorialFinished = GlobalApplication.prefs.getBoolean("isTutorialFinished")
        if (!isTutorialFinished) {
            startActivity(Intent(this, TutorialActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        // TODO: 로그인 되어 있지 않은 상태일 경우
        /*Glide.with(this).load(R.raw.logo).into(binding.ivLoading)
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 4500)*/

    }
}
