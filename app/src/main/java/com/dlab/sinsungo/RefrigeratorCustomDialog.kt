package com.dlab.sinsungo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.fragment.app.DialogFragment
import com.dlab.sinsungo.databinding.DialogRefrigeratorBinding
import java.text.SimpleDateFormat
import java.util.*

class RefrigeratorCustomDialog : DialogFragment() {
    private lateinit var binding: DialogRefrigeratorBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            binding = DialogRefrigeratorBinding.inflate(inflater)
            builder.setView(binding.root)
            builder.create()
        } ?: throw  IllegalStateException("Activity cannot be null")
    }
}
