package com.dlab.sinsungo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.content.res.ResourcesCompat
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
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.ref_custom_edit)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .create(), 0
        )

        binding.sdvRefrigerator.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_scan_receipt, R.drawable.btn_scan)
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.ref_scan_receipt)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .create(), 1
        )

        binding.sdvRefrigerator.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_picture_ingredient, R.drawable.btn_camera)
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.ref_picture_ingredient)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .create(), 2
        )
    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        when (actionItem?.id) {
            R.id.fab_custom_edit -> {
                openRefrigeratorDialog()
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
        setTitleSpanColor(R.color.royal_blue)
        initPopupMenus()
        dialogView.btnCanel.setOnClickListener { view: View ->
            dialog.dismiss()
        }
        dialogView.btnOpenDatePicker.setOnClickListener { view: View ->
            if (!dialogView.etExdate.text.isNullOrEmpty()) {
                parsingDate(dialogView.etExdate.text.toString())
            }
            DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Material_Light_Dialog_Alert,
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

    private fun initPopupMenus() {
        dialogView.btnOpenCategory.setOnClickListener {
            showRefCategories(dialogView.tvRefCategory, R.menu.menu_ref_categories)
        }
        dialogView.btnOpenCountType.setOnClickListener {
            showCountTypes(dialogView.tvCountType, R.menu.menu_count_type)
        }
        dialogView.btnOpenExdateType.setOnClickListener {
            showExdateTypes(dialogView.tvExdateType, R.menu.menu_exdate_type)
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
