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
import java.util.*

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
            binding.remain = calculateRemainDate(ingredientModel)
        }

        private fun calculateRemainDate(ingredientModel: IngredientModel): Long {
            val dDay = ingredientModel.exdate.time
            val today = Calendar.getInstance(Locale.KOREAN).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time.time

            return (dDay - today) / (24 * 60 * 60 * 1000)
        }
    }

    fun update(updated: List<IngredientModel>) {
        /*CoroutineScope(Dispatchers.Main).launch {
            val diffResult = async(Dispatchers.IO) {
                getDiffResult(updated)
            }
            ingredientList = updated
            diffResult.await().dispatchUpdatesTo(this@IngredientListAdapter)
        }*/
        // 코루틴으로 구현시 동작 안
        val diffResult = getDiffResult(updated)
        diffResult.dispatchUpdatesTo(this)
        ingredientList = updated
    }

    private fun getDiffResult(updated: List<IngredientModel>): DiffUtil.DiffResult {
        val diffCallback = IngredientDiffCallback(ingredientList, updated)
        return DiffUtil.calculateDiff(diffCallback)
    }
}
