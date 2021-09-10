package com.dlab.sinsungo.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlab.sinsungo.*
import com.dlab.sinsungo.adapters.DietListAdapter
import com.dlab.sinsungo.adapters.DietRatingListAdapter
import com.dlab.sinsungo.data.model.Diet
import com.dlab.sinsungo.data.model.DietRating
import com.dlab.sinsungo.databinding.DialogDietRatingBinding
import com.dlab.sinsungo.databinding.DialogShoppingBinding
import com.dlab.sinsungo.databinding.FragmentDietBinding
import com.dlab.sinsungo.ui.dialogs.CustomBottomSheetDiet
import com.dlab.sinsungo.utils.ItemDecoration
import com.dlab.sinsungo.utils.SwipeHelperCallback
import com.dlab.sinsungo.viewmodel.DietViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

class DietFragment : Fragment(), SpeedDialView.OnActionSelectedListener {
    private lateinit var binding: FragmentDietBinding
    private lateinit var dialog: CustomBottomSheetDiet
    private lateinit var dialogRating: AlertDialog
    private lateinit var dialogRatingView: DialogDietRatingBinding
    private lateinit var mDietListAdapter: DietListAdapter
    private lateinit var mDietRatingListAdapter: DietRatingListAdapter

    private var refId = GlobalApplication.prefs.getInt("refId")
    private val viewModel: DietViewModel by viewModels()
    private lateinit var swipeHelperCallback: SwipeHelperCallback


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDietBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.diets.observe(viewLifecycleOwner) {
            binding.listSize = it.size
        }
        initSpeedDialItem()
        initRcView()
        binding.sdvDiet.setOnActionSelectedListener(this)
        return binding.root
    }

    private fun initSpeedDialItem() {
        binding.sdvDiet.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_custom_diet, R.drawable.btn_edit)
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.diet_custom_edit)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .create(), 0
        )

        binding.sdvDiet.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_add_diet, R.drawable.ic_bottom_nav_recipe)
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .setFabSize(FloatingActionButton.SIZE_MINI)
                .setLabel(R.string.diet_add_edit)
                .setLabelClickable(false)
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.royal_blue, resources.newTheme()))
                .setLabelBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, resources.newTheme()))
                .create(), 1
        )
    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        when (actionItem?.id) {
            R.id.fab_custom_diet -> {
                dialog = CustomBottomSheetDiet(null)
                dialog.show(childFragmentManager, null)
                binding.sdvDiet.close()
                swipeHelperCallback.resetSwipe(binding.rcviewDiet)
            }
            R.id.fab_add_diet -> {
                binding.sdvDiet.close()
                (activity as MainActivity).changeBottomNavMenu(R.id.bottom_nav_menu_recipe)
            }
        }
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRcView() {
        swipeHelperCallback = SwipeHelperCallback().apply {
            setClamp(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, context?.resources?.displayMetrics))
            setType("diet")
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rcviewDiet)

        binding.rcviewDiet.apply {
            mDietListAdapter = DietListAdapter({ diet -> deleteDietItem(diet) }, { diet -> checkDietItem(diet) },
                { diet -> editDietItem(diet) })
            layoutManager = LinearLayoutManager(this.context)
            addItemDecoration(ItemDecoration())
            adapter = mDietListAdapter
            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }
    }

    private fun initDialog(diet: Diet) {
        dialogRatingView = DialogDietRatingBinding.inflate(layoutInflater)
        dialogRatingView.menuList = diet.dietMenus
        dialogRating = AlertDialog.Builder(context)
            .setView(dialogRatingView.root)
            .create()
        dialogRating.window!!.setBackgroundDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.bg_dialog_gray,
                null
            )
        )
        dialogRating.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialogRating.setCanceledOnTouchOutside(true)

        dialogRatingView.rcDietRating.apply {
            mDietRatingListAdapter = DietRatingListAdapter()
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = mDietRatingListAdapter
        }
        dialogRatingView.btnCancel.setOnClickListener {
            dialogRating.dismiss()
        }
        dialogRatingView.btnAccept.setOnClickListener {
            val dietRating = mDietRatingListAdapter.ratingDietList?.let { it1 ->
                DietRating(
                    refId,
                    it1,
                    diet.id
                )
            }
            if (dietRating != null) {
                viewModel.setDietRating(diet, dietRating)
            }
            dialogRating.dismiss()
        }
    }

    private fun deleteDietItem(diet: Diet) {
        viewModel.deleteDiet(diet)
        swipeHelperCallback.resetSwipe(binding.rcviewDiet)
    }

    private fun editDietItem(diet: Diet) {
        dialog = CustomBottomSheetDiet(diet)
        dialog.show(childFragmentManager, null)
        swipeHelperCallback.resetSwipe(binding.rcviewDiet)
    }

    private fun checkDietItem(diet: Diet) {
        initDialog(diet)
        dialogRating.show()
    }

}
