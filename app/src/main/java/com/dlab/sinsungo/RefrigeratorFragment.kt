package com.dlab.sinsungo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.dlab.sinsungo.databinding.DialogRefrigeratorBinding
import com.dlab.sinsungo.databinding.FragmentRefrigeratorBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import java.text.SimpleDateFormat
import java.util.*

class RefrigeratorFragment : Fragment(), SpeedDialView.OnActionSelectedListener {
    private lateinit var binding: FragmentRefrigeratorBinding
    private lateinit var dialogView: DialogRefrigeratorBinding
    private lateinit var dialog: AlertDialog

    private val mCalendar = Calendar.getInstance()

    private val mOnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRefrigeratorBinding.inflate(inflater, container, false)
        initTabLayout()
        initSpeedDialItem()
        binding.sdvRefrigerator.setOnActionSelectedListener(this)
        return binding.root
    }

    private fun initTabLayout() {
        val tabTextList = resources.getStringArray(R.array.ref_categories)
        binding.pagerIngredient.adapter = CustomFragmentStateAdapter(activity!!)
        TabLayoutMediator(binding.tablayoutRefrigerator, binding.pagerIngredient) { tab, position ->
            tab.text = tabTextList[position]
        }.attach()
    }

    private fun initSpeedDialItem() {
        binding.sdvRefrigerator.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_custom_edit, R.drawable.btn_edit)
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, context?.theme))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.ref_custom_edit)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, context?.theme))
                .create(), 0
        )

        binding.sdvRefrigerator.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_scan_receipt, R.drawable.btn_scan)
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, context?.theme))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.ref_scan_receipt)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, context?.theme))
                .create(), 1
        )

        binding.sdvRefrigerator.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_picture_ingredient, R.drawable.btn_camera)
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, context?.theme))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.ref_picture_ingredient)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, context?.theme))
                .create(), 2
        )
    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        when (actionItem?.id) {
            R.id.fab_custom_edit -> {
                openRefrigeratorDialog()
                binding.sdvRefrigerator.close()
            }
        }
        return true
    }

    private fun openRefrigeratorDialog() {
        dialogView = DialogRefrigeratorBinding.inflate(layoutInflater)
        dialog = AlertDialog.Builder(context)
            .setView(dialogView.root)
            .create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(false)
        init()
        dialog.show()
    }

    private fun init() {
        setTitleSpanColor(ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme))
        initPopupMenus()
        setTextWatcher()
        dialogView.btnCanel.setOnClickListener { view: View ->
            dialog.dismiss()
        }
        dialogView.btnOpenDatePicker.setOnClickListener { view: View ->
            DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Material_Dialog_Alert,
                mOnDateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setTitleSpanColor(color: Int) {
        val title = dialogView.tvDialogTitle.text
        val spannableString = SpannableString(title)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            7,
            9,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        dialogView.tvDialogTitle.text = spannableString
    }

    private fun setTextWatcher() {
        dialogView.etIngredient.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isEmpty() || input.isBlank()) {
                    dialogView.tvInputNoti1.setTextColor(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    dialogView.tvInputNoti1.visibility = View.VISIBLE
                    dialogView.ivIngredientCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    dialogView.clIngredientInput.background.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                } else {
                    dialogView.tvInputNoti1.visibility = View.GONE
                    dialogView.ivIngredientCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.dim_grey, context?.theme)
                    )
                    dialogView.clIngredientInput.background.setTint(
                        ResourcesCompat.getColor(resources, R.color.dim_grey, context?.theme)
                    )
                }
            }
        })
        dialogView.etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isEmpty() || input.isBlank()) {
                    dialogView.tvInputNoti2.setTextColor(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    dialogView.tvInputNoti2.visibility = View.VISIBLE
                    dialogView.ivCountCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    dialogView.clCountInput.background.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                } else {
                    dialogView.tvInputNoti2.visibility = View.GONE
                    dialogView.ivCountCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.dim_grey, context?.theme)
                    )
                    dialogView.clCountInput.background.setTint(
                        ResourcesCompat.getColor(resources, R.color.dim_grey, context?.theme)
                    )
                }
            }
        })
        dialogView.etExdate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                val dateRegex = Regex("(19|20)\\d{2}.(0[1-9]|1[012]).(0[1-9]|[12][0-9]|3[01])")
                if (input.isEmpty() || input.isBlank()) {
                    dialogView.tvInputNoti3.text = resources.getString(R.string.dial_normal_essential)
                    dialogView.tvInputNoti3.setTextColor(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    dialogView.tvInputNoti3.visibility = View.VISIBLE
                    dialogView.btnOpenDatePicker.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    dialogView.clExdateInput.background.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                } else if (!dateRegex.matches(input)) {
                    dialogView.tvInputNoti3.text = resources.getString(R.string.dial_exdate_mismatch_regex)
                    dialogView.tvInputNoti3.setTextColor(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    dialogView.tvInputNoti3.visibility = View.VISIBLE
                    dialogView.btnOpenDatePicker.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    dialogView.clExdateInput.background.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                } else {
                    dialogView.tvInputNoti3.visibility = View.GONE
                    dialogView.btnOpenDatePicker.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.dim_grey, context?.theme)
                    )
                    dialogView.clExdateInput.background.setTint(
                        ResourcesCompat.getColor(resources, R.color.dim_grey, context?.theme)
                    )
                    parsingDate(dialogView.etExdate.text.toString())
                }
            }
        })
    }

    private fun initPopupMenus() {
        dialogView.btnOpenCategory.setOnClickListener {
            showRefCategories(dialogView.clIngredientCategory, R.menu.menu_ref_categories)
        }
        dialogView.btnOpenCountType.setOnClickListener {
            showCountTypes(dialogView.clCountType, R.menu.menu_count_type)
        }
        dialogView.btnOpenExdateType.setOnClickListener {
            showExdateTypes(dialogView.clExdateType, R.menu.menu_exdate_type)
        }
    }

    private fun showRefCategories(view: View, @MenuRes menuRes: Int) {
        val refCategoryPopup = PopupMenu(context, view)
        refCategoryPopup.menuInflater.inflate(menuRes, refCategoryPopup.menu)

        refCategoryPopup.setOnMenuItemClickListener { menuItem: MenuItem ->
            dialogView.tvRefCategory.text = menuItem.title
            true
        }

        refCategoryPopup.show()
    }

    private fun showCountTypes(view: View, @MenuRes menuRes: Int) {
        val countTypePopup = PopupMenu(context, view)
        countTypePopup.menuInflater.inflate(menuRes, countTypePopup.menu)

        countTypePopup.setOnMenuItemClickListener { menuItem: MenuItem ->
            dialogView.tvCountType.text = menuItem.title
            true
        }

        countTypePopup.show()
    }

    private fun showExdateTypes(view: View, @MenuRes menuRes: Int) {
        val exdateTypePopup = PopupMenu(context, view)
        exdateTypePopup.menuInflater.inflate(menuRes, exdateTypePopup.menu)

        exdateTypePopup.setOnMenuItemClickListener { menuItem: MenuItem ->
            dialogView.tvExdateType.text = menuItem.title
            true
        }

        exdateTypePopup.show()
    }

    private fun updateLabel() {
        val mDateFormat = "yyyy.MM.dd"
        val mSimpleDateFormat = SimpleDateFormat(mDateFormat, Locale.KOREA)
        val mDateString = mSimpleDateFormat.format(mCalendar.time)

        dialogView.etExdate.setText(mDateString)
    }

    @SuppressLint("SimpleDateFormat")
    private fun parsingDate(date: String) {
        val mSimpleDateFormat = SimpleDateFormat("yyyy.MM.dd")
        val mDate = mSimpleDateFormat.parse(date)
        mCalendar.time = mDate!!
    }
}
