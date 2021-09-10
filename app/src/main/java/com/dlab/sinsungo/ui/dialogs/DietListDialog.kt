package com.dlab.sinsungo.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlab.sinsungo.adapters.DietListAdapter
import com.dlab.sinsungo.data.model.Diet
import com.dlab.sinsungo.databinding.DialogDietListBinding
import com.dlab.sinsungo.viewmodel.DietViewModel

class DietListDialog(private val name: String, private val dismiss: () -> Unit) : DialogFragment() {
    private lateinit var binding: DialogDietListBinding
    private val viewModel: DietViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogDietListBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.rvDietList.apply {
            adapter = DietListAdapter(editClick = { diet -> editDietItem(diet) })
            layoutManager = LinearLayoutManager(this.context)
            setHasFixedSize(true)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    private fun editDietItem(diet: Diet) {
        CustomBottomSheetDiet(diet, name, dismiss).show(
            childFragmentManager,
            "Custom_bottom_sheet_diet"
        )
    }
}
