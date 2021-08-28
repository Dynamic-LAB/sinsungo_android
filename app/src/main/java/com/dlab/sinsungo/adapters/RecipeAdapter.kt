package com.dlab.sinsungo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.data.model.Recipe
import com.dlab.sinsungo.databinding.ItemPbLoadingBinding
import com.dlab.sinsungo.databinding.ItemRcviewRecipeBinding
import com.dlab.sinsungo.databinding.ItemRcviewRecommendBinding

class RecipeAdapter(val type: String = "", val todoItemClick: (Recipe) -> Unit) :
    ListAdapter<Recipe, RecyclerView.ViewHolder>(RecipeDiffUtil) {
    private val VIEW_TYPE_ITEM = if (type == "normal") 0 else 1
    private val VIEW_TYPE_LOADING = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)

                if (VIEW_TYPE_ITEM == 0) {
                    val binding = ItemRcviewRecipeBinding.inflate(layoutInflater, parent, false)
                    RecipeViewHolder(binding)
                } else {
                    val binding = ItemRcviewRecommendBinding.inflate(layoutInflater, parent, false)
                    RecommendViewHolder(binding)
                }
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPbLoadingBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).name) {
            "" -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecipeViewHolder) {
            holder.bind(getItem(position))
        } else if (holder is RecommendViewHolder) {
            holder.bind(getItem(position))
        }
    }

    inner class RecipeViewHolder(private val binding: ItemRcviewRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.recipe = recipe
            binding.inRef = recipe.inRefIngredients.distinct().joinToString(", ")
            binding.notInRef = recipe.notInRefIngredients.joinToString(", ")
            binding.executePendingBindings()

            binding.root.setOnClickListener {
                todoItemClick(recipe)
            }
            binding.root.setOnLongClickListener {

                true
            }
        }
    }

    inner class RecommendViewHolder(private val binding: ItemRcviewRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.recipe = recipe
            binding.executePendingBindings()

            binding.root.setOnClickListener {
                todoItemClick(recipe)
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ItemPbLoadingBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    companion object RecipeDiffUtil : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}
