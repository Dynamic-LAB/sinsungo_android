package com.dlab.sinsungo

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.dlab.sinsungo.data.model.User
import com.dlab.sinsungo.databinding.DialogConfirmBinding
import com.dlab.sinsungo.ui.LoginActivity
import com.dlab.sinsungo.viewmodel.MyPageViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.data.OAuthLoginState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomConfirmDialog(private val type: String) : DialogFragment() {
    private lateinit var binding: DialogConfirmBinding
    private val viewModel: MyPageViewModel by activityViewModels()
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        init()

        when (type) {
            "로그아웃" -> binding.tvMemo.text = "로그아웃 하시겠습니까?"
            "회원 탈퇴" -> binding.tvMemo.text = "회원탈퇴 하시겠습니까?"
            "멤버 추가하기" -> {
                binding.tvMemo.text = viewModel.refrigerator.value?.inviteKey
                binding.btnAccept.text = "복사"
            }
            else -> binding.tvMemo.text = "멤버를 추방하시겠습니까?"
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    private fun init() {
        binding = DialogConfirmBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.view = this
        binding.lifecycleOwner = this

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    fun accept() {
        when (type) {
            "로그아웃" -> logout()
            "회원 탈퇴" -> widhdraw()
            "멤버 추가하기" -> copyInviteKey()
            else -> viewModel.removeMember(type.toInt()) { dismiss() }
        }
    }

    private fun logout() {
        when (GlobalApplication.prefs.getString("loginType")) {
            "kakao" -> kakaoLogout()
            "naver" -> naverLogout()
            else -> googleLogout()
        }
    }

    private fun kakaoLogout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.d("logout", "logout fail $error")
            } else {
                Log.d("logout", "logout success")
            }
            viewModel.updateUser(getCurrentUser()) { startLoginActivity() }
        }
    }

    private fun naverLogout() {
        OAuthLogin.getInstance().logout(requireContext())
        viewModel.updateUser(getCurrentUser()) { startLoginActivity() }
    }

    private fun googleLogout() {
        mGoogleSignInClient.signOut().addOnCompleteListener {
            Log.d("logout", "logout success")
            viewModel.updateUser(getCurrentUser()) { startLoginActivity() }
        }.addOnFailureListener {
            Log.d("logout", "logout fail")
        }
    }

    private fun widhdraw() {
        when (GlobalApplication.prefs.getString("loginType")) {
            "kakao" -> kakaoWithdraw()
            "naver" -> naverWithdraw()
            else -> googleWithdraw()
        }
    }

    private fun kakaoWithdraw() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.d("withdraw", "withdraw fail $error")
            } else {
                Log.d("withdraw", "withdraw success")
                viewModel.deleteUser(getCurrentUser()) { startLoginActivity() }
            }
        }
    }

    private fun naverWithdraw() {
        CoroutineScope(Dispatchers.IO).launch {
            if (OAuthLoginState.NEED_REFRESH_TOKEN == OAuthLogin.getInstance().getState(requireContext())) {
                OAuthLogin.getInstance().refreshAccessToken(requireContext())
            }

            OAuthLogin.getInstance().logoutAndDeleteToken(requireContext()).let {
                if (!it) {
                    val errorCode: String = OAuthLogin.getInstance().getLastErrorCode(context).code
                    val errorDesc = OAuthLogin.getInstance().getLastErrorDesc(context)

                    Log.e("api_error", "errorCode: $errorCode, errorDesc: $errorDesc")
                }
                viewModel.deleteUser(getCurrentUser()) { startLoginActivity() }
            }
        }
    }

    private fun googleWithdraw() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener {
            Log.d("withdraw", "withdraw success")
            viewModel.deleteUser(getCurrentUser()) { startLoginActivity() }
        }.addOnFailureListener {
            Log.d("withdraw", "withdraw fail")
        }
    }

    private fun startLoginActivity() {
        GlobalApplication.prefs.remove("userId")
        GlobalApplication.prefs.remove("loginType")
        GlobalApplication.prefs.remove("name")
        GlobalApplication.prefs.remove("pushToken")
        GlobalApplication.prefs.remove("refId")
        GlobalApplication.prefs.remove("pushSetting")

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags =
            (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun getCurrentUser(): User {
        val userId = GlobalApplication.prefs.getString("userId")!!
        val loginType = GlobalApplication.prefs.getString("loginType")!!
        val name = GlobalApplication.prefs.getString("name")!!
        val pushToken = ""
        val refId = GlobalApplication.prefs.getInt("refId")
        val pushSetting = GlobalApplication.prefs.getInt("pushSetting")
        return User(userId, loginType, name, pushToken, refId, pushSetting)
    }

    private fun copyInviteKey() {
        val clipboardManager = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("CODE", binding.tvMemo.text.trim())
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(context, "코드가 복사되었습니다.", Toast.LENGTH_SHORT).show()
        dismiss()
    }
}
