package com.dlab.sinsungo.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dlab.sinsungo.R
import com.dlab.sinsungo.RecipeToDietDialog
import com.dlab.sinsungo.data.model.Recipe
import com.dlab.sinsungo.databinding.ActivityRecipeDetailBinding
import com.dlab.sinsungo.viewmodel.RecipeDetailViewModel

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeDetailBinding
    private val viewModel: RecipeDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        setRecipeData()
    }

    private fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_detail)

        binding.lifecycleOwner = this
        binding.view = this
        binding.viewModel = viewModel
    }

    private fun setRecipeData() {
        intent.getParcelableExtra<Recipe>("recipe")?.let { viewModel.setRecipe(it) }
    }

    fun viewRecipe(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    fun addDiet(name: String) {
        RecipeToDietDialog(name).show(supportFragmentManager, null)
    }
}
