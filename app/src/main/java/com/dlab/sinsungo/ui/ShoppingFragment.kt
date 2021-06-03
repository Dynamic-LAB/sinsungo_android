package com.dlab.sinsungo.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import androidx.collection.LruCache
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.*
import com.dlab.sinsungo.adapters.ShoppingListAdapter
import com.dlab.sinsungo.data.model.IngredientModel
import com.dlab.sinsungo.data.model.Shopping
import com.dlab.sinsungo.databinding.DialogShoppingBinding
import com.dlab.sinsungo.databinding.DialogShoppingToRefBinding
import com.dlab.sinsungo.databinding.FragmentShoppingBinding
import com.dlab.sinsungo.utils.ItemDecoration
import com.dlab.sinsungo.utils.SwipeHelperCallback
import com.dlab.sinsungo.viewmodel.IngredientViewModel
import com.dlab.sinsungo.viewmodel.ShoppingViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ShoppingFragment : Fragment(), SpeedDialView.OnActionSelectedListener {
    private lateinit var binding: FragmentShoppingBinding
    private lateinit var dialogView: DialogShoppingBinding
    private lateinit var checkDialogView: DialogShoppingToRefBinding
    private lateinit var dialog: AlertDialog

    private lateinit var mShoppingListAdapter: ShoppingListAdapter
    private lateinit var swipeHelperCallback: SwipeHelperCallback

    private val viewModel: ShoppingViewModel by viewModels()
    private val ingredientViewModel: IngredientViewModel by activityViewModels()
    private var refId = GlobalApplication.prefs.getInt("refId")

    private val mCalendar = Calendar.getInstance()

    private val mOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel()
    }

    private val mOnClickOpenDatePicker = View.OnClickListener {

        val datePicker = DatePickerDialog(
            requireContext(),
            mOnDateSetListener,
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

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
                if (mShoppingListAdapter.currentList.size == 0) {
                    setAlertDialog(resources.getString(R.string.dial_empty_share_data))
                } else {
                    shareScreenshot()
                }
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
        setTextWatcher()
        initPopupMenus()
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun initCheckDialog(shopping: Shopping) {
        checkDialogView = DialogShoppingToRefBinding.inflate(layoutInflater)
        dialog = AlertDialog.Builder(context)
            .setView(checkDialogView.root)
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
        setTitleSpanColor(
            checkDialogView.tvDialogTitle,
            ResourcesCompat.getColor(
                resources,
                R.color.royal_blue,
                context?.theme
            ),
            7, 9
        )
        updateLabel()
        refPopupMenus()
        setCheckTextWatcher()
        checkDialogView.clExdateInput.setOnClickListener(mOnClickOpenDatePicker)
        checkDialogView.etIngredient.setText(shopping.shopName)
        checkDialogView.etCount.setText(shopping.shopAmount.toString())
        checkDialogView.tvCountType.text = (shopping.shopUnit)
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun checkDialogSetting(shopping: Shopping) {
        checkDialogView.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        checkDialogView.btnAccept.setOnClickListener {
            val newIngredient = IngredientModel(
                refId,
                checkDialogView.etIngredient.text.toString(),
                checkDialogView.etCount.text.toString().toInt(),
                checkDialogView.tvExdateInput.text.toString(),
                checkDialogView.tvRefCategory.text.toString(),
                checkDialogView.tvCountType.text.toString(),
                checkDialogView.tvExdateType.text.toString()
            )
            val inputIngredient = mutableListOf<IngredientModel>()
            inputIngredient.add(newIngredient)
            ingredientViewModel.requestPostIngredient(inputIngredient)
            deleteShoppingItem(shopping)
            dialog.dismiss()
        }
    }

    private fun dialogSetting(shopping: Shopping?) {
        var id = refId
        if (shopping != null) {
            dialogView.tvDialogTitle.text = getString(R.string.shop_modify)
            id = shopping.id
            dialogView.etIngredient.setText(shopping.shopName)
            dialogView.etCount.setText(shopping.shopAmount.toString())
            dialogView.tvCountType.text = shopping.shopUnit
            if (shopping.shopMemo != null) {
                dialogView.etMemo.setText(shopping.shopMemo)
            }
        }
        setTitleSpanColor(
            dialogView.tvDialogTitle,
            ResourcesCompat.getColor(
                resources,
                R.color.royal_blue,
                context?.theme
            ),
            8, 10
        )
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
                    viewModel.editShopping(refId, shopping, newShopping)
                } else {
                    viewModel.setShopping(newShopping)
                }
                dialog.dismiss()
            }
        }
    }

    private fun refPopupMenus() {
        checkDialogView.clIngredientCategory.setOnClickListener {
            showRefCategories(checkDialogView.clIngredientCategory)
        }
        checkDialogView.clCountType.setOnClickListener {
            showRefCountTypes(checkDialogView.clCountType)
        }
        checkDialogView.clExdateType.setOnClickListener {
            showExDateTypes(checkDialogView.clExdateType)
        }
    }

    private fun showRefCategories(view: View) {
        val refCategoryPopup = PopupMenu(requireContext(), view)
        refCategoryPopup.menuInflater.inflate(R.menu.menu_ref_categories, refCategoryPopup.menu)

        refCategoryPopup.setOnMenuItemClickListener { menuItem: MenuItem ->
            checkDialogView.tvRefCategory.text = menuItem.title.toString()
            true
        }

        refCategoryPopup.show()
    }

    private fun showRefCountTypes(view: View) {
        val countTypePopup = PopupMenu(requireContext(), view)
        countTypePopup.menuInflater.inflate(R.menu.menu_count_type, countTypePopup.menu)

        countTypePopup.setOnMenuItemClickListener { menuItem: MenuItem ->
            checkDialogView.tvCountType.text = menuItem.title.toString()
            true
        }

        countTypePopup.show()
    }

    private fun showExDateTypes(view: View) {
        val exDateTypePopup = PopupMenu(requireContext(), view)
        exDateTypePopup.menuInflater.inflate(R.menu.menu_exdate_type, exDateTypePopup.menu)

        exDateTypePopup.setOnMenuItemClickListener { menuItem: MenuItem ->
            checkDialogView.tvExdateType.text = menuItem.title.toString()
            true
        }
        exDateTypePopup.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRcView() {
        swipeHelperCallback = SwipeHelperCallback().apply {
            setClamp(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, context?.resources?.displayMetrics))
            setType("shopping")
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rcviewShopping)

        binding.rcviewShopping.apply {
            mShoppingListAdapter = ShoppingListAdapter({ shopping -> deleteShoppingItem(shopping) },
                { shopping -> editShoppingItem(shopping) },
                { shopping -> checkShoppingItem(shopping) })
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
        swipeHelperCallback.resetSwipe(binding.rcviewShopping)
    }

    private fun editShoppingItem(shopping: Shopping) {
        initDialog()
        dialogSetting(shopping)
        swipeHelperCallback.resetSwipe(binding.rcviewShopping)
        dialog.show()
    }

    private fun checkShoppingItem(shopping: Shopping) {
        initCheckDialog(shopping)
        checkDialogSetting(shopping)
        swipeHelperCallback.resetSwipe(binding.rcviewShopping)
        dialog.show()
    }

    private fun initPopupMenus() {
        dialogView.btnOpenCountType.setOnClickListener {
            showCountTypes(dialogView.clCountType)
        }
    }

    private fun showCountTypes(view: View) {
        val countTypePopup = PopupMenu(context, view)
        countTypePopup.menuInflater.inflate(R.menu.menu_count_type, countTypePopup.menu)
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

    private fun setCheckTextWatcher() {
        checkDialogView.etIngredient.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isEmpty() || input.isBlank()) {
                    checkDialogView.tvInputNoti1.visibility = View.VISIBLE
                    checkDialogView.ivIngredientCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    checkDialogView.lineUnderIngredientInput.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.free_speech_red) }
                    checkDialogView.lineUnderIngredientCategory.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.free_speech_red) }
                } else {
                    checkDialogView.tvInputNoti1.visibility = View.GONE
                    checkDialogView.ivIngredientCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme)
                    )
                    checkDialogView.lineUnderIngredientInput.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.royal_blue) }
                    checkDialogView.lineUnderIngredientCategory.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.royal_blue) }
                }
            }
        })

        checkDialogView.etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isBlank() || input.isEmpty()) {
                    checkDialogView.tvInputNoti2.visibility = View.VISIBLE
                    checkDialogView.ivCountCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.free_speech_red, context?.theme)
                    )
                    checkDialogView.lineUnderCountInput.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.free_speech_red) }
                    checkDialogView.lineUnderCountType.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.free_speech_red) }
                } else if (input == "0") {
                    checkDialogView.tvInputNoti2.visibility = View.GONE
                    checkDialogView.ivCountCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.dim_grey, context?.theme)
                    )
                    checkDialogView.lineUnderCountInput.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.dim_grey) }
                    checkDialogView.lineUnderCountType.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.dim_grey) }
                } else {
                    checkDialogView.tvInputNoti2.visibility = View.GONE
                    checkDialogView.ivCountCutlery.drawable.setTint(
                        ResourcesCompat.getColor(resources, R.color.royal_blue, context?.theme)
                    )
                    checkDialogView.lineUnderCountInput.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.royal_blue) }
                    checkDialogView.lineUnderCountType.background =
                        context?.let { ContextCompat.getDrawable(it, R.color.royal_blue) }
                }
            }
        })

    }

    private fun setTitleSpanColor(view: TextView, color: Int, start: Int, end: Int) {
        val title = view.text
        val spannableString = SpannableString(title)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.text = spannableString
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

    private fun updateLabel() {
        val mDateFormat = "yyyy-MM-dd"
        val mSimpleDateFormat = SimpleDateFormat(mDateFormat, Locale.KOREA)
        val mDateString = mSimpleDateFormat.format(mCalendar.time)

        checkDialogView.tvExdateInput.text = mDateString
    }

    private fun setAlertDialog(resource: String) {
        this.context?.let {
            MaterialAlertDialogBuilder(it)
                .setMessage(resource)
                .setPositiveButton(resources.getString(R.string.btn_accept)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun shareScreenshot() {
        try {
            val cachePath = File(this.context?.cacheDir, "images")
            cachePath.mkdirs() // don't forget to make the directory
            val stream =
                FileOutputStream("$cachePath/image.png") // overwrites this image every time
            getScreenshotFromRecyclerView(binding.rcviewShopping)?.compress(
                Bitmap.CompressFormat.PNG,
                100,
                stream
            )
            stream.close()
            val newFile = File(cachePath, "image.png")
            val contentUri: Uri? = this.context?.let {
                FileProvider.getUriForFile(
                    it,
                    "com.dlab.sinsungo.fileprovider", newFile
                )
            }
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "image/png"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            startActivity(Intent.createChooser(sharingIntent, "Share image"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Suppress("DEPRECATION")
    private fun getScreenshotFromRecyclerView(view: RecyclerView): Bitmap? {
        val adapter = view.adapter
        var bigBitmap: Bitmap? = null
        if (adapter != null) {
            val size = adapter.itemCount
            var height = 0
            val paint = Paint()
            var iHeight = 32
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmaCache =
                LruCache<String, Bitmap>(cacheSize)
            for (i in 0 until size) {
                val holder =
                    adapter.createViewHolder(view, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(
                        view.width,
                        View.MeasureSpec.EXACTLY
                    ),
                    View.MeasureSpec.makeMeasureSpec(
                        0,
                        View.MeasureSpec.UNSPECIFIED
                    )
                )
                holder.itemView.layout(
                    0,
                    0,
                    holder.itemView.measuredWidth,
                    holder.itemView.measuredHeight
                )
                holder.itemView.isDrawingCacheEnabled = true
                holder.itemView.buildDrawingCache()
                val drawingCache = holder.itemView.drawingCache
                if (drawingCache != null) {
                    bitmaCache.put(i.toString(), drawingCache)
                }
                height += holder.itemView.measuredHeight
            }
            if (height < view.height) height = view.height
            bigBitmap =
                Bitmap.createBitmap(view.measuredWidth, height, Bitmap.Config.ARGB_8888)
            val bigCanvas = Canvas(bigBitmap)
            bigCanvas.drawColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.white_smoke,
                    context?.theme
                )
            )
            for (i in 0 until size) {
                val bitmap = bitmaCache[i.toString()]
                bigCanvas.drawBitmap(bitmap!!, 0f, iHeight.toFloat(), paint)
                iHeight += bitmap.height
                bitmap.recycle()
            }
        }
        return bigBitmap
    }

}
