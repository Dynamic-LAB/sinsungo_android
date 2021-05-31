package com.dlab.sinsungo

import android.R.color
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlab.sinsungo.data.model.Diet
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

class CustomBottomSheetDiet(private val oldDiet: Diet?) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDietBinding
    private val mCalendar = Calendar.getInstance()
    private val viewModel: DietViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var chipList = mutableListOf<String?>()

    private var refId = 5
    private lateinit var mIngredientListAdapter: DietIngredientListAdapter

    private val mOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel(Color.parseColor(resources.getString(R.string.color_royal_blue)))
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogDietBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        dialog?.setOnShowListener {
            val bottomSheet = it as BottomSheetDialog
            val sheetInternal: View = bottomSheet.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(sheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
            BottomSheetBehavior.from(sheetInternal).isDraggable = false
        }

        dialogInit()
        updateLabel(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
        binding.etIngredientSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.search(binding.etIngredientSearch.text.toString())
                    hideKeyboard()
                    return true
                }
                return false
            }
        })

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.clearUseIngredients()
    }

    private fun editSetting(diet: Diet) {
        viewModel.setUseIngredients(diet.dietIngredients)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
        val dateValue = dateFormat.parse(diet.dietDate)
        mCalendar.time = dateValue!!
        updateLabel(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
        binding.etMemo.setText(diet.dietMemo)
        val menus = diet.dietMenus.filterNotNull()
        for (element in menus) {
            element.let { binding.flexMenu.addNewChip(it) }
        }
        if (menus.size == 10) {
            binding.etMenuName.height = 0
            binding.etMenuName.text = null
            binding.etMenuName.hint = null
            binding.etMenuName.setBackgroundColor(resources.getColor(color.transparent))
            binding.etMenuName.clearFocus()
            binding.etMenuName.isClickable = false
        }
    }

    private fun dialogInit() {
        binding.rcDietIngredient.apply {
            mIngredientListAdapter = if (oldDiet == null) {
                DietIngredientListAdapter(null)
            } else {
                editSetting(oldDiet)
                DietIngredientListAdapter(viewModel.useIngredients.value)
            }
            layoutManager = LinearLayoutManager(this.context)
            adapter = mIngredientListAdapter
        }

        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnSave.setOnClickListener {
            for (i in 1..(10 - chipList.size)) {
                chipList.add(null)
            }
            val mDateFormat = "yyyy-MM-dd"
            val mSimpleDateFormat = SimpleDateFormat(mDateFormat, Locale.KOREA)
            val mDateString = mSimpleDateFormat.format(mCalendar.time)
            if (oldDiet == null) {
                val newDiet = Diet(
                    refId,
                    binding.etMemo.text.toString(),
                    mDateString,
                    chipList,
                    mIngredientListAdapter.ingredientList
                )
                viewModel.setDiet(newDiet)
                this.dismiss()
            } else {
                val newDiet = Diet(
                    oldDiet.id,
                    binding.etMemo.text.toString(),
                    mDateString,
                    chipList,
                    mIngredientListAdapter.ingredientList.distinct()
                )
                viewModel.editDiet(5, oldDiet, newDiet)
                this.dismiss()
            }

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

    fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etIngredientSearch.windowToken, 0)
    }
}

fun Context.dpToPx(dp: Int): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).roundToInt()

