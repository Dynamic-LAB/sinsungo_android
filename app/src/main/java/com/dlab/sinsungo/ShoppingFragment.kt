package com.dlab.sinsungo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
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

    private lateinit var mShoppingListAdapter: ShoppingListAdapter

    private val viewModel: ShoppingViewModel by viewModels()
    private val REF_ID = 5

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentShoppingBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.shoppings.observe(viewLifecycleOwner) {
            binding.listSize = it.size
        }
        binding.sdvShopping.setOnActionSelectedListener(this)

        initSpeedDialItem()
        initRcView()

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
                dialogSetting(null)
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
        setTitleSpanColor(
            ResourcesCompat.getColor(
                resources,
                R.color.royal_blue,
                context?.theme
            )
        )
        setTextWatcher()
        initPopupMenus()
        dialog.setCanceledOnTouchOutside(false)
    }


    private fun dialogSetting(shopping: Shopping?) {
        var id = REF_ID
        if (shopping != null) {
            id = shopping.id
            dialogView.etIngredient.setText(shopping.shopName)
            dialogView.etCount.setText(shopping.shopAmount.toString())
            dialogView.tvCountType.text = shopping.shopUnit
            if (shopping.shopMemo != null) {
                dialogView.etMemo.setText(shopping.shopMemo)
            }
        }
        dialogView.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialogView.btnAccept.setOnClickListener {
            val ingredientName = dialogView.etIngredient.text.toString()
            val ingredientCount = dialogView.etCount.text.toString()
            val newShopping: Shopping
            if ((ingredientName.isEmpty() || ingredientName.isBlank()) && (ingredientCount.isEmpty() || ingredientCount.isBlank())) {
                dialogView.tvInputNoti1.visibility = View.VISIBLE
                dialogView.tvInputNoti2.visibility = View.VISIBLE
                setDialogColor(0, R.color.free_speech_red)
                setDialogColor(1, R.color.free_speech_red)
            } else if (ingredientName.isEmpty() || ingredientName.isBlank()) {
                dialogView.tvInputNoti1.visibility = View.VISIBLE
                setDialogColor(0, R.color.free_speech_red)
            } else if (ingredientCount.isEmpty() || ingredientCount.isBlank()) {
                dialogView.tvInputNoti2.visibility = View.VISIBLE
                setDialogColor(1, R.color.free_speech_red)
            } else {
                newShopping =
                    Shopping(
                        ingredientName,
                        ingredientCount.toInt(),
                        dialogView.tvCountType.text.toString(),
                        dialogView.etMemo.text.toString(),
                        id
                    )
                if (shopping != null) {
                    viewModel.editShopping(REF_ID, shopping, newShopping)
                } else {
                    viewModel.setShopping(newShopping)
                }
                dialog.dismiss()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRcView() {
        val swipeHelperCallback = SwipeHelperCallback().apply {
            setClamp(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, context?.resources?.displayMetrics))
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rcviewShopping)

        binding.rcviewShopping.apply {
            mShoppingListAdapter = ShoppingListAdapter({ shopping -> deleteShoppingItem(shopping) },
                { shopping -> editShoppingItem(shopping) })
            layoutManager = LinearLayoutManager(this.context)
            addItemDecoration(ItemDecoration())
            adapter = mShoppingListAdapter
            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }
    }

    private fun deleteShoppingItem(shopping: Shopping) {
        viewModel.deleteShopping(shopping)
    }

    private fun editShoppingItem(shopping: Shopping) {
        initDialog()
        dialogSetting(shopping)
        dialog.show()
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
                    setDialogColor(2, R.color.dim_grey)
                } else {
                    setDialogColor(2, R.color.royal_blue)
                }
            }
        })
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
                dialogView.clCountType.background.setTint(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.dim_grey,
                        context?.theme
                    )
                )
            }
            2 -> {
                dialogView.btnMemo.drawable.setTint(newColor)
                dialogView.clMemoInput.background.setTint(newColor)
            }
        }
    }
}
