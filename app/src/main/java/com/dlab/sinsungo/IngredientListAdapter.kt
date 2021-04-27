package com.dlab.sinsungo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.databinding.ItemRcviewIngredientBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class IngredientListAdapter(var ingredientList: List<IngredientModel>) :
    RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemRcviewIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(ingredientList[position])
    }

    override fun getItemCount(): Int {
        return ingredientList.size
    }

    class IngredientViewHolder(val binding: ItemRcviewIngredientBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredientModel: IngredientModel) {
            binding.data = ingredientModel
        }
    }

    fun update(updated: List<IngredientModel>) {
        CoroutineScope(Dispatchers.Main).launch {
            val diffResult = async(Dispatchers.IO) {
                getDiffResult(updated)
            }
            ingredientList = updated
            diffResult.await().dispatchUpdatesTo(this@IngredientListAdapter)
        }
    }

    fun getDiffResult(updated: List<IngredientModel>): DiffUtil.DiffResult {
        val diffCallback = IngredientDiffCallback(ingredientList, updated)
        return DiffUtil.calculateDiff(diffCallback)
    }
}
