package com.dlab.sinsungo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dlab.sinsungo.MainActivity
import com.dlab.sinsungo.R
import com.dlab.sinsungo.TutorialActivity
import com.dlab.sinsungo.databinding.ActivityLoginBinding
import com.dlab.sinsungo.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var pushToken: String
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val uid = account.id!!
                val nickname = account.displayName!!

                viewModel.isRegister(uid, "google", nickname, "")
            } catch (e: ApiException) {
                Log.e("api_error", "Google sign in failed", e)
                e.printStackTrace()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    // 초기화 함수
    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setObserver()

        val mOAuthLogin = OAuthLogin.getInstance()
        mOAuthLogin.init(
            this,
            getString(R.string.naver_client_id),
            getString(R.string.naver_client_secret),
            getString(R.string.app_name_kor)
        )

        val naverLoginHandler = NaverLoginHandler(this, viewModel)

        binding.btnLoginNaver.setOAuthLoginHandler(naverLoginHandler)
    }

    // observer 등록
    private fun setObserver() {
        viewModel.tryAuth.observe(this) {
            it.getContentIfNotHandled()?.let { type ->
                when (type) {
                    "kakao" -> kakaoAuth()
                    "google" -> googleAuth()
                }
            }
        }
        // 유저의 냉장고 여부에 따라 액티비티 이동
        viewModel.newUser.observe(this) {
            viewModel.updatePushToken {
                val intent: Intent by lazy {
                    when (it.refId) {
                        null -> Intent(this, TutorialActivity::class.java).putExtra("isFromSplash", false)
                        else -> Intent(this, MainActivity::class.java)
                    }
                }
                startActivity(intent)
                finish()
            }
        }
    }

    // 카카오 인증
    private fun kakaoAuth() {
        val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            } else if (token != null) {
                UserApiClient.instance.me { user, userError ->
                    user?.let {
                        val nickname = user.kakaoAccount?.profile?.nickname!!
                        val uid = user.id.toString()
                        viewModel.isRegister(uid, "kakao", nickname, "")
                    }
                    userError?.let {
                        Log.e("user_error", userError.message.toString())
                    }
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = kakaoCallback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
        }
    }

    // 네이버 인증
    private class NaverLoginHandler(
        private val context: Context,
        private val viewModel: LoginViewModel,
    ) :
        OAuthLoginHandler() {
        override fun run(success: Boolean) {
            if (success) {
                val accessToken = OAuthLogin.getInstance().getAccessToken(context)
                val url = "https://openapi.naver.com/v1/nid/me"

                // 코루틴으로 비동기로 네이버 회원 정보 api 호출
                CoroutineScope(Dispatchers.IO).launch {
                    OAuthLogin.getInstance().requestApi(context, accessToken, url).let {
                        if (it != null) {
                            val jsonObject = JSONObject(it)
                            val response = jsonObject.getJSONObject("response")
                            viewModel.isRegister(
                                response.getString("id"),
                                "naver",
                                response.getString("name"),
                                ""
                            )
                        } else {
                            val errorCode: String = OAuthLogin.getInstance().getLastErrorCode(context).code
                            val errorDesc = OAuthLogin.getInstance().getLastErrorDesc(context)

                            Log.e("api_error", "errorCode: $errorCode, errorDesc: $errorDesc")
                        }
                    }
                }
            } else {
                val errorCode: String = OAuthLogin.getInstance().getLastErrorCode(context).code
                val errorDesc = OAuthLogin.getInstance().getLastErrorDesc(context)

                Log.e("api_error", "errorCode: $errorCode, errorDesc: $errorDesc")
            }
        }
    }

    // 구글 인증
    private fun googleAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent

        startForResult.launch(signInIntent)
    }
}
