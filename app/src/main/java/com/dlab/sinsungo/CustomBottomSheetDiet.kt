package com.dlab.sinsungo

import android.R.color
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlab.sinsungo.databinding.DialogDietBinding
import com.dlab.sinsungo.viewmodel.DietViewModel
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CustomBottomSheetDiet : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDietBinding
    private val mCalendar = Calendar.getInstance()
    private val viewModel: DietViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private val chipList = mutableListOf<String>()

    var tracker: SelectionTracker<Long>? = null
    private lateinit var mIngredientListAdapter: DietIngredientListAdapter

    private val mOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel(Color.parseColor(resources.getString(R.string.color_royal_blue)))
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogDietBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        dialog?.setOnShowListener {
            val bottomSheet = it as BottomSheetDialog
            val sheetInternal: View = bottomSheet.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(sheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
            BottomSheetBehavior.from(sheetInternal).isDraggable = false
        }
        dialogInit()
        trackSetting()
        updateLabel(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))


        return binding.root
    }

    private fun dialogInit() {
        binding.rcDietIngredient.apply {
            mIngredientListAdapter = DietIngredientListAdapter()
            layoutManager = LinearLayoutManager(this.context)
            adapter = mIngredientListAdapter
        }
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
                when {
                    name == "" -> {
                        Toast.makeText(context, "내용을 입력해주세요!", Toast.LENGTH_SHORT).show()
                    }
                    chipList.size == 9 -> {
                        binding.flexMenu.addNewChip(name)
                        binding.etMenuName.height = 0
                        et.text = null
                        et.hint = null
                        et.setBackgroundColor(resources.getColor(color.transparent))
                        et.clearFocus()
                        et.isClickable = false
                    }
                    name in chipList -> {
                        Toast.makeText(context, "이미 존재하는 메뉴입니다!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.flexMenu.addNewChip(name)
                        et.text = null
                    }
                }
            }
            false
        }
    }

    private fun trackSetting() {
        tracker = SelectionTracker.Builder<Long>(
            "mySelection",
            binding.rcDietIngredient,
            StableIdKeyProvider(binding.rcDietIngredient),
            MyItemDetailsLookup(binding.rcDietIngredient),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        mIngredientListAdapter.tracker = tracker

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


    private fun FlexboxLayout.addNewChip(content: String) {
        val chip = LayoutInflater.from(context).inflate(R.layout.chip, null) as Chip

        chip.text = content
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.WRAP_CONTENT,
            ViewGroup.MarginLayoutParams.WRAP_CONTENT
        )
        layoutParams.rightMargin = context.dpToPx(4)
        chip.setOnCloseIconClickListener {
            if (chipList.size <= 10) {
                binding.etMenuName.height = 16
                binding.etMenuName.hint = getString(R.string.dial_hint_edit_menu)
                binding.etMenuName.setBackgroundResource(androidx.appcompat.R.drawable.abc_edit_text_material)
                binding.etMenuName.isClickable = true
            }
            removeView(chip as View)
            chipList.remove(chip.text)
        }
        addView(chip, childCount - 1, layoutParams)
        chipList.add(content)
    }
}

fun Context.dpToPx(dp: Int): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).roundToInt()

