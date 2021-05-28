package com.dlab.sinsungo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.dlab.sinsungo.databinding.DialogIngredientSelfInputBinding
import java.text.SimpleDateFormat
import java.util.*

class IngredientSelfInputDialog : DialogFragment() {
    private lateinit var binding: DialogIngredientSelfInputBinding
    private val viewModel: IngredientViewModel by activityViewModels()

    private val mCalendar = Calendar.getInstance()

    private val mOnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel()
    }

    private val mOnClickOpenDatePicker = View.OnClickListener { view: View ->
        val datePicker = DatePickerDialog(
            requireContext(),
            mOnDateSetListener,
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.setOnDismissListener(mDatePickerDismissListener)
        datePicker.show()
    }

    private val mDatePickerDismissListener = DialogInterface.OnDismissListener {
        if (viewModel.inputIngredient.value!!.exdate.isEmpty() || viewModel.inputIngredient.value!!.exdate.isBlank()) {
            binding.tvInputNoti3.visibility = View.VISIBLE
            binding.ivExdateCalendar.drawable.setTint(
                ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
            )
            binding.lineUnderExdateInput.background =
                context?.let { ContextCompat.getDrawable(it, R.color.free_speech_red) }
        } else {
            binding.tvInputNoti3.visibility = View.GONE
            binding.ivExdateCalendar.drawable.setTint(
                ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme)
            )
            binding.lineUnderExdateInput.background =
                context?.let { ContextCompat.getDrawable(it, R.color.royal_blue) }
        }
//        if (binding.tvExdateInput.text.isEmpty() || binding.tvExdateInput.text.isBlank()) {
//            binding.tvInputNoti3.visibility = View.VISIBLE
//            binding.ivExdateCalendar.drawable.setTint(
//                ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
//            )
//            binding.lineUnderExdateInput.background =
//                context?.let { ContextCompat.getDrawable(it, R.color.free_speech_red) }
//        } else {
//            binding.tvInputNoti3.visibility = View.GONE
//            binding.ivExdateCalendar.drawable.setTint(
//                ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme)
//            )
//            binding.lineUnderExdateInput.background =
//                context?.let { ContextCompat.getDrawable(it, R.color.royal_blue) }
//        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogIngredientSelfInputBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        initDialog()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun initDialog() {
        viewModel.dialogDismissFlag.observe(this) {
            if (it) {
                viewModel.setDialogDismissFlag(false)
                dismiss()
            }
        }

        setTextWatcher()
        initPopupMenus()

        binding.clExdateInput.setOnClickListener(mOnClickOpenDatePicker)
    }

    private fun updateLabel() {
        val mDateFormat = "yyyy-MM-dd"
        val mSimpleDateFormat = SimpleDateFormat(mDateFormat, Locale.KOREA)
        val mDateString = mSimpleDateFormat.format(mCalendar.time)

        viewModel.setInputIngredientValue("exdate", mDateString)
    }

    private fun setTextWatcher() {
        binding.etIngredient.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isEmpty() || input.isBlank()) {
                    binding.tvInputNoti1.visibility = View.VISIBLE
                    binding.ivIngredientCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    binding.lineUnderIngredientInput.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.free_speech_red) }
                } else {
                    binding.tvInputNoti1.visibility = View.GONE
                    binding.ivIngredientCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme)
                    )
                    binding.lineUnderIngredientInput.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.royal_blue) }
                }
            }
        })

        binding.etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isBlank() || input.isEmpty()) {
                    binding.tvInputNoti2.visibility = View.VISIBLE
                    binding.ivCountCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    binding.lineUnderCountInput.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.free_speech_red) }
                } else if (input == "0") {
                    binding.tvInputNoti2.visibility = View.GONE
                    binding.ivCountCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.dim_grey, context?.theme)
                    )
                    binding.lineUnderCountInput.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.dim_grey) }
                } else {
                    binding.tvInputNoti2.visibility = View.GONE
                    binding.ivCountCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme)
                    )
                    binding.lineUnderCountInput.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.royal_blue) }
                }
            }
        })

        if (viewModel.isModify.value == true) {
            binding.ivExdateCalendar.drawable.setTint(
                ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme)
            )
            binding.lineUnderExdateInput.background =
                context?.let { ContextCompat.getDrawable(it, R.color.royal_blue) }
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
            val dateValue = dateFormat.parse(viewModel.inputIngredient.value!!.exdate)
            mCalendar.time = dateValue
        }
    }

    private fun initPopupMenus() {
        binding.clIngredientCategory.setOnClickListener {
            showRefCategories(binding.clIngredientCategory, R.menu.menu_ref_categories)
        }
        binding.clCountType.setOnClickListener {
            showCountTypes(binding.clCountType, R.menu.menu_count_type)
        }
        binding.clExdateType.setOnClickListener {
            showExdateTypes(binding.clExdateType, R.menu.menu_exdate_type)
        }
    }

    private fun showRefCategories(view: View, @MenuRes menuRes: Int) {
        val refCategoryPopup = PopupMenu(requireContext(), view)
        refCategoryPopup.menuInflater.inflate(menuRes, refCategoryPopup.menu)

        refCategoryPopup.setOnMenuItemClickListener { menuItem: MenuItem ->
            viewModel.setInputIngredientValue("refCategory", menuItem.title.toString())
            true
        }

        refCategoryPopup.show()
    }

    private fun showCountTypes(view: View, @MenuRes menuRes: Int) {
        val countTypePopup = PopupMenu(requireContext(), view)
        countTypePopup.menuInflater.inflate(menuRes, countTypePopup.menu)

        countTypePopup.setOnMenuItemClickListener { menuItem: MenuItem ->
            viewModel.setInputIngredientValue("countType", menuItem.title.toString())
            true
        }

        countTypePopup.show()
    }

    private fun showExdateTypes(view: View, @MenuRes menuRes: Int) {
        val exdateTypePopup = PopupMenu(requireContext(), view)
        exdateTypePopup.menuInflater.inflate(menuRes, exdateTypePopup.menu)

        exdateTypePopup.setOnMenuItemClickListener { menuItem: MenuItem ->
            viewModel.setInputIngredientValue("exdateType", menuItem.title.toString())
            true
        }

        exdateTypePopup.show()
    }
}
