package com.dlab.sinsungo

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.dlab.sinsungo.databinding.DialogShoppingBinding
import com.dlab.sinsungo.databinding.FragmentShoppingBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

class ShoppingFragment : Fragment(), SpeedDialView.OnActionSelectedListener {
    private lateinit var binding: FragmentShoppingBinding
    private lateinit var dialogView: DialogShoppingBinding
    private lateinit var dialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentShoppingBinding.inflate(inflater, container, false)
        initSpeedDialItem()
        binding.sdvShopping.setOnActionSelectedListener(this)
        return binding.root
    }

    private fun initSpeedDialItem() {
        binding.sdvShopping.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_add_shopping, R.drawable.btn_edit)
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.shop_add)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .create(), 0
        )

        binding.sdvShopping.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_share_shopping, R.drawable.btn_share)
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.shop_share)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .create(), 1
        )
    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        when (actionItem?.id) {
            R.id.fab_share_shopping -> {
                //TODO
                binding.sdvShopping.close()
            }
            R.id.fab_add_shopping -> {
                dialogView = DialogShoppingBinding.inflate(layoutInflater)
                dialog = AlertDialog.Builder(context)
                    .setView(dialogView.root)
                    .create()
                dialog.window!!.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.bg_dialog_gray,
                        null
                    )
                )
                dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                init()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
                binding.sdvShopping.close()
            }
        }
        return true
    }

    private fun init() {
        setTitleSpanColor(Color.parseColor(resources.getString(R.string.color_royal_blue)))
        initPopupMenus()
        dialogView.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setTitleSpanColor(color: Int) {
        val title = dialogView.tvDialogTitle.text
        val spannableString = SpannableString(title)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            8,
            10,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        dialogView.tvDialogTitle.text = spannableString
    }

    private fun initPopupMenus() {
        dialogView.btnOpenCountType.setOnClickListener {
            showCountTypes(dialogView.clCountType, R.menu.menu_count_type)
        }
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
}
