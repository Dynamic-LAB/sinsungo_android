package com.dlab.sinsungo

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dlab.sinsungo.data.model.Shopping
import com.dlab.sinsungo.databinding.DialogShoppingBinding
import com.dlab.sinsungo.databinding.FragmentShoppingBinding
import com.dlab.sinsungo.viewmodel.ShoppingViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

class ShoppingFragment : Fragment(), SpeedDialView.OnActionSelectedListener {
    private lateinit var binding: FragmentShoppingBinding
    private lateinit var dialogView: DialogShoppingBinding
    private lateinit var dialog: AlertDialog

    private val viewModel: ShoppingViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentShoppingBinding.inflate(inflater, container, false)
        binding.sdvShopping.setOnActionSelectedListener(this)
        initSpeedDialItem()
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
                initDialog()
                dialog.show()
                binding.sdvShopping.close()
            }
        }
        return true
    }

    private fun initDialog() {
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
        setDialogColor(0, R.color.dim_grey)
        setDialogColor(1, R.color.dim_grey)
        setDialogColor(2, R.color.dim_grey)
        initShopping()
        initViewModel()
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun initViewModel() {
        dialogView.viewModel = viewModel
        dialogView.lifecycleOwner = this
        viewModel.shoppings.observe(viewLifecycleOwner) {
            Log.d("shopping result", it.toString())
        }
    }

    private fun initShopping() {
        setTitleSpanColor(Color.parseColor(resources.getString(R.string.color_royal_blue)))
        setTextWatcher()
        initPopupMenus()

        dialogView.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialogView.btnAccept.setOnClickListener {
            val ingredientName = dialogView.etIngredient.text.toString()
            val ingredientCount = dialogView.etCount.text.toString()
            if ((ingredientName.isEmpty() || ingredientName.isBlank()) && (ingredientCount.isEmpty() || ingredientCount.isBlank())){
                dialogView.tvInputNoti1.visibility = View.VISIBLE
                dialogView.tvInputNoti2.visibility = View.VISIBLE
                setDialogColor(0, R.color.free_speech_red)
                setDialogColor(1, R.color.free_speech_red)
            } else if (ingredientName.isEmpty() || ingredientName.isBlank()){
                dialogView.tvInputNoti1.visibility = View.VISIBLE
                setDialogColor(0, R.color.free_speech_red)
            } else if (ingredientCount.isEmpty() || ingredientCount.isBlank()){
                dialogView.tvInputNoti2.visibility = View.VISIBLE
                setDialogColor(1, R.color.free_speech_red)
            } else {
                val newShopping =
                    Shopping(
                        ingredientName,
                        ingredientCount.toInt(),
                        dialogView.tvCountType.text.toString(),
                        dialogView.etMemo.text.toString(),
                        5
                    )
                viewModel.setShopping(newShopping)
                dialog.dismiss()
            }

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

    private fun setTextWatcher() {
        dialogView.etIngredient.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isEmpty() || input.isBlank()) {
                    dialogView.tvInputNoti1.visibility = View.VISIBLE
                    setDialogColor(0, R.color.free_speech_red)
                } else {
                    dialogView.tvInputNoti1.visibility = View.GONE
                    setDialogColor(0, R.color.royal_blue)
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
                    dialogView.tvInputNoti2.visibility = View.VISIBLE
                    setDialogColor(1, R.color.free_speech_red)
                } else {
                    dialogView.tvInputNoti2.visibility = View.GONE
                    setDialogColor(1, R.color.royal_blue)
                }
            }
        })
        dialogView.etMemo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isEmpty() || input.isBlank()) {
                    setDialogColor(2, R.color.free_speech_red)
                } else {
                    setDialogColor(2, R.color.dim_grey)
                }
            }
        })
    }

    private fun setDialogColor(choice: Int, color: Int) {
        val newColor = ResourcesCompat.getColor(resources, color, context?.theme)
        when (choice) {
            0 -> {
                dialogView.ivIngredientCutlery.drawable.setTint(newColor)
                dialogView.clIngredientInput.background.setTint(newColor)
            }
            1 -> {
                dialogView.ivCountCutlery.drawable.setTint(newColor)
                dialogView.clCountInput.background.setTint(newColor)
            }
            2 -> {
                dialogView.btnMemo.drawable.setTint(newColor)
                dialogView.clMemoInput.background.setTint(newColor)
            }
        }
    }
}
