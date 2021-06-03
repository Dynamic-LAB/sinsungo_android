package com.dlab.sinsungo.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.dlab.sinsungo.MainActivity
import com.dlab.sinsungo.databinding.DialogInviteBinding
import com.dlab.sinsungo.viewmodel.TutorialViewModel

class MemberInviteDialog : DialogFragment() {
    private lateinit var binding: DialogInviteBinding
    private val viewModel: TutorialViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogInviteBinding.inflate(layoutInflater)
        binding.view = this
        binding.lifecycleOwner = this

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    fun accept() {
        viewModel.inviteUser(binding.etInviteKey.text.toString()) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags =
                (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}
