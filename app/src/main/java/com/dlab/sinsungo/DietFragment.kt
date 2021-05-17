package com.dlab.sinsungo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.dlab.sinsungo.databinding.FragmentDietBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

class DietFragment : Fragment(), SpeedDialView.OnActionSelectedListener {
    private lateinit var binding: FragmentDietBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDietBinding.inflate(inflater, container, false)
        initSpeedDialItem()
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
                val dialog = CustomBottomSheetDiet()
                dialog.show(requireActivity().supportFragmentManager, "Custom_bottom_sheet_diet")
                binding.sdvDiet.close()
            }
            R.id.fab_add_diet -> {
                binding.sdvDiet.close()
                (activity as MainActivity).changeBottomNavMenu(R.id.bottom_nav_menu_recipe)
            }
        }
        return true
    }

}
