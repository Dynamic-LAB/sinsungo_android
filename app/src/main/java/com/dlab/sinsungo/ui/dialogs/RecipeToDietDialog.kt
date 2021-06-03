package com.dlab.sinsungo.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.dlab.sinsungo.databinding.DialogRecipeToDietBinding
import com.dlab.sinsungo.viewmodel.DietViewModel

class RecipeToDietDialog(private val name: String) : DialogFragment() {
    private lateinit var binding: DialogRecipeToDietBinding
    private val viewModel: DietViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogRecipeToDietBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.view = this
        viewModel

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        return dialog
    }

    fun newDiet() {
        CustomBottomSheetDiet(null, name) { dismiss() }.show(childFragmentManager, null)
    }

    fun oldDiet() {
        DietListDialog(name) { dismiss() }.show(childFragmentManager, null)
    }
}
