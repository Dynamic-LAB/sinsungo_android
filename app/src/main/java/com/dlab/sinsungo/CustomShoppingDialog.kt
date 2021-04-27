package com.dlab.sinsungo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dlab.sinsungo.databinding.DialogDietBinding
import com.dlab.sinsungo.databinding.DialogRefrigeratorBinding
import com.dlab.sinsungo.databinding.DialogShoppingBinding
import java.text.SimpleDateFormat
import java.util.*

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
