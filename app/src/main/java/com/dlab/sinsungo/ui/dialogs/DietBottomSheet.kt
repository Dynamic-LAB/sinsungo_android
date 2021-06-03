package com.dlab.sinsungo.ui.dialogs

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
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.data.model.IngredientModel
import com.dlab.sinsungo.R
import com.dlab.sinsungo.adapters.DietIngredientListAdapter
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

class CustomBottomSheetDiet(
    private val oldDiet: Diet?,
    private val recipeName: String? = null,
    private val dismiss: (() -> Unit)? = null
) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogDietBinding
    private val mCalendar = Calendar.getInstance()
    private val viewModel: DietViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var chipList = mutableListOf<String?>()

    private var refId = GlobalApplication.prefs.getInt("refId")
    private lateinit var mIngredientListAdapter: DietIngredientListAdapter

    private val mOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateCalenderLabel(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogDietBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.useIngredients.observe(viewLifecycleOwner) {
            binding.listSize = it.size + viewModel.unUseIngredients.value?.size!!
            Log.d("listSize", binding.listSize.toString())
        }
        viewModel.unUseIngredients.observe(viewLifecycleOwner) {
            binding.listSize = it.size + viewModel.useIngredients.value?.size!!
            Log.d("listSize", binding.listSize.toString())
        }

        dialog?.setOnShowListener {
            val bottomSheet = it as BottomSheetDialog
            val sheetInternal: View = bottomSheet.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(sheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
            BottomSheetBehavior.from(sheetInternal).isDraggable = false
        }

        dialogInit()

        updateCalenderLabel(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))

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
        binding.tvTitleAdd.text = resources.getString(R.string.dial_modify)
        diet.dietIngredients?.let { viewModel.setUseIngredients(it) }
        setCalender(diet.dietDate)
        binding.etMemo.setText(diet.dietMemo)
        val menus = diet.dietMenus.filterNotNull()
        for (element in menus) {
            element.let { binding.flexMenu.addNewChip(it) }
        }
    }

    private fun setCalender(date: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
        val dateValue = dateFormat.parse(date)
        mCalendar.time = dateValue!!
        updateCalenderLabel(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
    }

    private fun getCalender(pattern: String): String = SimpleDateFormat(pattern, Locale.KOREA).format(mCalendar.time)

    private fun dialogInit() {
        binding.rcDietIngredient.apply {
            mIngredientListAdapter = if (oldDiet == null) {
                viewModel.setCreate()
                DietIngredientListAdapter(
                    { ingredient -> toUseIngredient(ingredient) },
                    { ingredient -> toUnUseIngredient(ingredient) },
                    viewModel.useIngredients.value
                )
            } else {
                editSetting(oldDiet)
                DietIngredientListAdapter(
                    { ingredient -> toUseIngredient(ingredient) },
                    { ingredient -> toUnUseIngredient(ingredient) },
                    viewModel.useIngredients.value
                )
            }
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = mIngredientListAdapter
        }

        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }
        binding.btnSave.setOnClickListener {
            if (chipList.isEmpty()) {
                Toast.makeText(context, "메뉴는 필수로 추가하셔야됩니다!", Toast.LENGTH_SHORT).show()
            } else {
                for (i in 1..(10 - chipList.size)) {
                    chipList.add(null)
                }

                if (oldDiet == null) {
                    val newDiet = Diet(
                        refId,
                        binding.etMemo.text.toString(),
                        getCalender("yyyy-MM-dd"),
                        chipList,
                        mIngredientListAdapter.ingredientList
                    )
                    viewModel.setDiet(newDiet, dismiss)
                    this.dismiss()
                } else {
                    val newDiet = Diet(
                        oldDiet.id,
                        binding.etMemo.text.toString(),
                        getCalender("yyyy-MM-dd"),
                        chipList,
                        mIngredientListAdapter.ingredientList.distinct()
                    )
                    viewModel.editDiet(refId, oldDiet, newDiet, dismiss)
                    this.dismiss()
                }
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
                binding.flexMenu.addNewChip(name)
                et.text = null
            }
            false
        }

        recipeName?.let { binding.flexMenu.addNewChip(it) }
    }

    @SuppressLint("ResourceAsColor")
    private fun updateCalenderLabel(color: Int) {
        val spannableString = SpannableString(getCalender("yyyy년 MM월 dd일"))
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

    private fun checkChipCount() = if (chipList.size == 10) {
        binding.etMenuName.clearFocus()
        binding.etMenuName.setBackgroundColor(Color.TRANSPARENT)
        binding.etMenuName.height = 0
        binding.etMenuName.text = null
        binding.etMenuName.hint = null
        binding.etMenuName.isEnabled = false
    } else {
        binding.etMenuName.height = 100
        binding.etMenuName.hint = getString(R.string.dial_hint_edit_menu)
        binding.etMenuName.setBackgroundResource(androidx.appcompat.R.drawable.abc_edit_text_material)
        binding.etMenuName.isEnabled = true
    }

    private fun checkChipName(name: String): Boolean {
        return when {
            name.isBlank() -> {
                Toast.makeText(context, "내용을 입력해주세요!", Toast.LENGTH_SHORT).show()
                false
            }
            name in chipList -> {
                Toast.makeText(context, "이미 존재하는 메뉴입니다!", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun FlexboxLayout.addNewChip(content: String) {
        val chip = LayoutInflater.from(context).inflate(R.layout.chip, null) as Chip
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.WRAP_CONTENT,
            ViewGroup.MarginLayoutParams.WRAP_CONTENT
        )
        layoutParams.rightMargin = context.dpToPx(4)
        if (chipList.size == 10) {
            Toast.makeText(context, "메뉴는 10개까지 입력이 가능합니다! ", Toast.LENGTH_SHORT).show()
        } else if (checkChipName(content)) {
            chip.text = content
            chip.setOnCloseIconClickListener {
                removeView(chip as View)
                chipList.remove(chip.text)
                checkChipCount()
            }
            addView(chip, childCount - 1, layoutParams)
            chipList.add(content)
            checkChipCount()
        }

    }

    private fun toUseIngredient(ingredient: IngredientModel) {
        viewModel.toUseIngredient(ingredient)
    }

    private fun toUnUseIngredient(ingredient: IngredientModel) {
        viewModel.toUnUseIngredient(ingredient)
    }

    fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etIngredientSearch.windowToken, 0)
    }
}

fun Context.dpToPx(dp: Int): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).roundToInt()

