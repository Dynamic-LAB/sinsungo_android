package com.dlab.sinsungo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dlab.sinsungo.databinding.FragmentIngredientBinding

class IngredientFragment(private val refCategory: String) : Fragment() {
    private lateinit var binding: FragmentIngredientBinding
    private lateinit var viewModel: IngredientViewModel
    private lateinit var viewModelFactory: IngredientViewModelFactory
    private lateinit var mIngredientListAdapter: IngredientListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentIngredientBinding.inflate(inflater, container, false)

        initViewModel()
        getIngredients(5, refCategory)

        return binding.root
    }

    fun initViewModel() {
        viewModelFactory = IngredientViewModelFactory(IngredientRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(IngredientViewModel::class.java)

        viewModel.ingredients.observe(viewLifecycleOwner) {
            initRcView(it)
            Log.d("cur data", it.toString())
        }
    }

    private fun initRcView(ingredients: List<IngredientModel>) {
        if (::mIngredientListAdapter.isInitialized) {
            mIngredientListAdapter.update(ingredients)
        } else {
            mIngredientListAdapter = IngredientListAdapter(ingredients.filter { it.refCategory == refCategory })

            binding.rcviewIngredient.run {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = mIngredientListAdapter
            }
        }
        binding.listSize = ingredients.size
    }

    private fun getIngredients(refID: Int, refCategory: String) {
        viewModel.requestIngredients(refID, refCategory)
    }
}
