package com.dlab.sinsungo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.dlab.sinsungo.databinding.FragmentRefrigeratorBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

class RefrigeratorFragment : Fragment(), SpeedDialView.OnActionSelectedListener {
    private lateinit var binding: FragmentRefrigeratorBinding
    private val viewModel: IngredientViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRefrigeratorBinding.inflate(inflater, container, false)
        initTabLayout()
        initSpeedDialItem()
        binding.sdvRefrigerator.setOnActionSelectedListener(this)
        return binding.root
    }

    private fun initTabLayout() {
        val tabTextList = resources.getStringArray(R.array.ref_categories)
        binding.pagerIngredient.apply {
            adapter = CustomFragmentStateAdapter(childFragmentManager, lifecycle)
            offscreenPageLimit = 4
        }
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
                val dialog = RefrigeratorSelfInputIngredientDialog()
                viewModel.setModify(false)
                dialog.show(childFragmentManager, "input ingredient dialog")
                binding.sdvRefrigerator.close()
            }
        }
        return true
    }
}
