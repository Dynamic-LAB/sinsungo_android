package com.dlab.sinsungo

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    companion object {
        lateinit var prefs: MySharedPreference
    }

    override fun onCreate() {
        prefs = MySharedPreference(applicationContext)
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        //KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}
