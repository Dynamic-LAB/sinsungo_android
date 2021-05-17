package com.dlab.sinsungo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.dlab.sinsungo.databinding.DialogDietBinding
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CustomBottomSheetDiet : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDietBinding
    private val mCalendar = Calendar.getInstance()

    private val mOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel(Color.parseColor(resources.getString(R.string.color_royal_blue)))
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogDietBinding.inflate(inflater, container, false)
        init()
        updateLabel(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
        return binding.root
    }

    private fun init() {
        //TODO: 이미 추가 되어 있는 메뉴 추가

        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnSave.setOnClickListener {
            //TODO: 저장하는 과정 추가
        }
        binding.constDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                mOnDateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.etMenuName.setOnEditorActionListener { v, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                val et = v as EditText
                val name = et.text.toString()
                if (name == "") {
                    Toast.makeText(context, "내용을 입력해주세요!", Toast.LENGTH_SHORT).show()
                } else {
                    binding.flexMenu.addNewChip(name, false)
                    et.text = null
                }
            }
            false
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun updateLabel(color: Int) {
        val mDateFormat = "yyyy년 MM월 dd일"
        val mSimpleDateFormat = SimpleDateFormat(mDateFormat, Locale.KOREA)
        val mDateString = mSimpleDateFormat.format(mCalendar.time)
        val spannableString = SpannableString(mDateString)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            0,
            4,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(color),
            6,
            8,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(color),
            10,
            12,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvBottomTitleDate.text = spannableString
    }


    private fun FlexboxLayout.addNewChip(content: String, Flag: Boolean) {
        val chip = LayoutInflater.from(context).inflate(R.layout.chip, null) as Chip
        if (Flag) {
            chip.setChipBackgroundColorResource(R.color.almond_frost50)
        }
        chip.text = content
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.WRAP_CONTENT,
            ViewGroup.MarginLayoutParams.WRAP_CONTENT
        )
        layoutParams.rightMargin = context.dpToPx(4)
        chip.setOnCloseIconClickListener { removeView(chip as View) }
        addView(chip, childCount - 1, layoutParams)
    }
}

fun Context.dpToPx(dp: Int): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).roundToInt()

