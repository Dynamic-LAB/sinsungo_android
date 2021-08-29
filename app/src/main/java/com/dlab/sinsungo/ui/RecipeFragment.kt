package com.dlab.sinsungo.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.GlobalApplication
import com.dlab.sinsungo.adapters.RecipeAdapter
import com.dlab.sinsungo.data.model.Recipe
import com.dlab.sinsungo.databinding.FragmentRecipeBinding
import com.dlab.sinsungo.viewmodel.RecipeViewModel

class RecipeFragment : Fragment() {
    private lateinit var binding: FragmentRecipeBinding
    private val viewModel: RecipeViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var rvLayoutManager: LinearLayoutManager
    private val refId = GlobalApplication.prefs.getInt("refId")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRecipeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setRecyclerView()
        setScrollListener()
        setSearchListener()

        return binding.root
    }

    private fun setRecyclerView() {
        binding.vpRecommend.apply {
            adapter = RecipeAdapter("recommend") { recipe -> moveToDetail(recipe) }
        }

        binding.rvRecipe.apply {
            recipeAdapter = RecipeAdapter("normal") { recipe -> moveToDetail(recipe) }
            layoutManager = LinearLayoutManager(this.context)
            setHasFixedSize(true)
            adapter = recipeAdapter
            rvLayoutManager = this.layoutManager as LinearLayoutManager
        }
    }

    private fun setScrollListener() {
        binding.rvRecipe.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = rvLayoutManager.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount

                if (itemTotalCount > 20 && !binding.rvRecipe.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount - 1
                    && !viewModel.trigger
                ) {
                    viewModel.getRecipe(refId, itemTotalCount - 1, 20, binding.etSearch.text.toString())
                }
            }
        })
    }

    private fun setSearchListener() {
        binding.etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val inputMethodManager = activity!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                    viewModel.enableSearch(refId, 0, 20, binding.etSearch.text.toString())
                    inputMethodManager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
                    binding.etSearch.clearFocus()

                    return true
                }
                return false
            }
        })
    }

    private fun moveToDetail(recipe: Recipe) {
        val intent = Intent(this.context, RecipeDetailActivity::class.java)
        intent.putExtra("recipe", recipe)
        startActivity(intent)
    }
}
