package com.dlab.sinsungo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.databinding.ItemRcviewIngredientBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class IngredientListAdapter :
    ListAdapter<IngredientModel, IngredientListAdapter.IngredientViewHolder>(IngredientDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemRcviewIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class IngredientViewHolder(val binding: ItemRcviewIngredientBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredientModel: IngredientModel) {
            binding.data = ingredientModel
            binding.remain = calculateRemainDate(ingredientModel)
            binding.executePendingBindings()
        }

        private fun calculateRemainDate(ingredientModel: IngredientModel): Long {
            val dDay = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).parse(ingredientModel.exdate).time
            val today = Calendar.getInstance(Locale.KOREAN).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time.time

            return (dDay - today) / (24 * 60 * 60 * 1000)
        }
    }

    companion object IngredientDiffUtil : DiffUtil.ItemCallback<IngredientModel>() {
        override fun areContentsTheSame(oldItem: IngredientModel, newItem: IngredientModel): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: IngredientModel, newItem: IngredientModel): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
