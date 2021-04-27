package com.dlab.sinsungo

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.dlab.sinsungo.databinding.DialogShoppingBinding

class CustomShoppingDialog : DialogFragment() {
    private lateinit var binding: DialogShoppingBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            binding = DialogShoppingBinding.inflate(inflater)
            builder.setView(binding.root)
            builder.create()
        } ?: throw  IllegalStateException("Activity cannot be null")
    }
}
