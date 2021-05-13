package com.dlab.sinsungo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.dlab.sinsungo.databinding.DialogRefrigeratorBinding
import java.text.SimpleDateFormat
import java.util.*

class RefrigeratorCustomDialog : DialogFragment() {
    private lateinit var binding: DialogRefrigeratorBinding
    private val viewModel: IngredientViewModel by viewModels(ownerProducer = { requireParentFragment() })

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
        if (binding.tvExdateInput.text.isEmpty() || binding.tvExdateInput.text.isBlank()) {
            binding.lineUnderExdateInput.background =
                context?.let { ContextCompat.getDrawable(it, R.color.free_speech_red) }
            binding.tvInputNoti3.setTextColor(
                ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
            )
            binding.tvInputNoti3.visibility = View.VISIBLE
            binding.btnOpenDatePicker.drawable.setTint(
                ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
            )
        } else {
            binding.btnOpenDatePicker.drawable.setTint(
                ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme)
            )
            binding.lineUnderExdateInput.background =
                context?.let { ContextCompat.getDrawable(it, R.color.royal_blue) }
            binding.tvInputNoti3.visibility = View.GONE
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogRefrigeratorBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        initDialog()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    /*override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogRefrigeratorBinding.inflate(layoutInflater)
        initDialog()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog?.setCanceledOnTouchOutside(false)
        return binding.root
    }*/

    private fun initDialog() {
        viewModel.postFlag.observe(this) {
            if (it) {
                dismiss()
            }
        }

        setTitleSpanColor(ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme))
        setTextWatcher()
        initPopupMenus()

        binding.clExdateInput.setOnClickListener(mOnClickOpenDatePicker)

        binding.lineUnderIngredientInput.background =
            context?.let { ContextCompat.getDrawable(it, R.color.dim_grey) }
        binding.lineUnderCountInput.background =
            context?.let { ContextCompat.getDrawable(it, R.color.dim_grey) }

        binding.btnAccept.setOnClickListener { view: View ->
            val addIngredient = IngredientModel(
                6,
                binding.etIngredient.text.toString(),
                binding.etCount.text.toString().toInt(),
                binding.tvExdateInput.text.toString(),
                binding.tvRefCategory.text.toString(),
                binding.tvCountType.text.toString(),
                binding.tvExdateType.text.toString()
            )
            viewModel.requestPostIngredients(addIngredient)
        }

        binding.btnCanel.setOnClickListener { view: View ->
            dismiss()
        }
    }

    private fun updateLabel() {
        val mDateFormat = "yyyy-MM-dd"
        val mSimpleDateFormat = SimpleDateFormat(mDateFormat, Locale.KOREA)
        val mDateString = mSimpleDateFormat.format(mCalendar.time)

        binding.tvExdateInput.setText(mDateString)

        binding.btnOpenDatePicker.drawable.setTint(
            ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme)
        )
        binding.lineUnderExdateInput.background =
            context?.let { ContextCompat.getDrawable(it, R.color.royal_blue) }
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

    private fun setTextWatcher() {
        binding.etIngredient.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isEmpty() || input.isBlank()) {
                    binding.tvInputNoti1.setTextColor(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
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
                if (input.isEmpty() || input.isBlank()) {
                    binding.tvInputNoti2.setTextColor(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    binding.tvInputNoti2.visibility = View.VISIBLE
                    binding.ivCountCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    binding.lineUnderCountInput.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.free_speech_red) }
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
            binding.tvRefCategory.text = menuItem.title
            true
        }

        refCategoryPopup.show()
    }

    private fun showCountTypes(view: View, @MenuRes menuRes: Int) {
        val countTypePopup = PopupMenu(requireContext(), view)
        countTypePopup.menuInflater.inflate(menuRes, countTypePopup.menu)

        countTypePopup.setOnMenuItemClickListener { menuItem: MenuItem ->
            binding.tvCountType.text = menuItem.title
            true
        }

        countTypePopup.show()
    }

    private fun showExdateTypes(view: View, @MenuRes menuRes: Int) {
        val exdateTypePopup = PopupMenu(requireContext(), view)
        exdateTypePopup.menuInflater.inflate(menuRes, exdateTypePopup.menu)

        exdateTypePopup.setOnMenuItemClickListener { menuItem: MenuItem ->
            binding.tvExdateType.text = menuItem.title
            true
        }

        exdateTypePopup.show()
    }
}
