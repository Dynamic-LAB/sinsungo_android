package com.dlab.sinsungo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.dlab.sinsungo.databinding.DialogDietBinding
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import java.util.*
import kotlin.math.roundToInt

class CustomBottomSheetDiet : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDietBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogDietBinding.inflate(inflater, container, false)
        binding.etMenuName.setOnEditorActionListener { v, action, event ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                val et = v as EditText
                val name = et.text.toString()
                if (name == "") {
                    Toast.makeText(context, "내용을 입력해주세요!", Toast.LENGTH_SHORT).show()
                } else {
                    binding.flexMenu.addNewChip(name)
                    et.text = null
                }
            }
            false
        }

        return binding.root
    }

    private fun FlexboxLayout.addNewChip(content: String) {
        val chip = LayoutInflater.from(context).inflate(R.layout.chip, null) as Chip
        //chip.setChipBackgroundColorResource(R.color.almond_frost50)
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

