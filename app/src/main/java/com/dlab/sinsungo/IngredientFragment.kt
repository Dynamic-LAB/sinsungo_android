package com.dlab.sinsungo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlab.sinsungo.databinding.FragmentIngredientBinding

class IngredientFragment(private val refCategory: String) : Fragment() {
    private lateinit var binding: FragmentIngredientBinding
    private val viewModel: IngredientViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private lateinit var mIngredientListAdapter: IngredientListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentIngredientBinding.inflate(inflater, container, false)
        binding.category = refCategory
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initRcView()
        initSortMenu()

        viewModel.ingredients.observe(viewLifecycleOwner) {
            binding.listSize = it.filter { ingredientModel -> ingredientModel.refCategory == refCategory }.size
        }

        return binding.root
    }

    private fun initRcView() {
        binding.rcviewIngredient.apply {
            mIngredientListAdapter = IngredientListAdapter({ ingredientModel ->  deleteIngredient(ingredientModel) })
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = mIngredientListAdapter
        }
    }

    private fun initSortMenu() {
        binding.clIngredientSort.setOnClickListener { view: View ->
            showSortMenu(view, R.menu.menu_ingredient_sortby)
        }
    }

    private fun showSortMenu(view: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            binding.tvSort.text = menuItem.title
            var data = mIngredientListAdapter.currentList
            when (menuItem.title.toString()) {
                "재료명 순" -> data = data.sortedBy { it.name }
                "신선도 순" -> {
                    val exDateList = data.filter { it.exdateType == "유통기한" }
                    val notExDateList = data.filter { it.exdateType != "유통기한" }
                    data = exDateList.sortedBy { it.exdate } + notExDateList.sortedBy { it.exdate }
                }
                "최근 추가 순" -> data = data.sortedBy { it.id }
            }
            mIngredientListAdapter.submitList(data)
            true
        }

        popup.show()
    }

    fun deleteIngredient(ingredientModel: IngredientModel) {
        viewModel.requestDeleteIngredient(ingredientModel)
    }
}
