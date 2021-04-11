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
    private val mCalendar = Calendar.getInstance()

    private val mOnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            binding = DialogRefrigeratorBinding.inflate(inflater)

            builder.setView(binding.root)

            init()

            builder.create()
        } ?: throw  IllegalStateException("Activity cannot be null")
    }

    private fun init() {
        setTitleSpanColor(R.color.royal_blue)
        binding.btnCanel.setOnClickListener { view: View ->
            this.dismiss()
        }
        binding.btnOpenDatePicker.setOnClickListener { view: View ->
            if (!binding.etExdate.text.isNullOrEmpty()) {
                parsingDate(binding.etExdate.text.toString())
            }
            DatePickerDialog(
                context!!,
                android.R.style.Widget_Material_Light_DatePicker,
                mOnDateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setTitleSpanColor(color: Int) {
        val title = binding.tvDialogTitle.text
        val spannableString = SpannableString(title)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            7,
            9,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvDialogTitle.text = spannableString
    }

    private fun updateLabel() {
        val mDateFormat = "yyyy.MM.dd"
        val mSimpleDateFormat = SimpleDateFormat(mDateFormat, Locale.KOREA)
        val mDateString = mSimpleDateFormat.format(mCalendar.time)

        binding.etExdate.setText(mDateString)
    }

    @SuppressLint("SimpleDateFormat")
    private fun parsingDate(date: String) {
        val mSimpleDateFormat = SimpleDateFormat("yyyy.MM.dd")
        val mDate = mSimpleDateFormat.parse(date)
        mCalendar.time = mDate!!
    }
}
