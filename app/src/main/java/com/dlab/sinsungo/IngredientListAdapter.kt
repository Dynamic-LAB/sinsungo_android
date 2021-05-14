package com.dlab.sinsungo

import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.databinding.ItemRcviewIngredientBinding
import java.text.SimpleDateFormat
import java.util.*

class IngredientListAdapter(
    val deleteMenuClick: (IngredientModel) -> Unit,
    val updateMenuClick: (IngredientModel) -> Unit
) :
    ListAdapter<IngredientModel, IngredientListAdapter.IngredientViewHolder>(IngredientDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemRcviewIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class IngredientViewHolder(val binding: ItemRcviewIngredientBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnCreateContextMenuListener {
        fun bind(ingredientModel: IngredientModel) {
            binding.data = ingredientModel
            binding.remain = calculateRemainDate(ingredientModel)
            binding.root.setOnCreateContextMenuListener(this)
        }

        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        private fun calculateRemainDate(ingredientModel: IngredientModel): Long {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
            val dDay = dateFormat.parse(ingredientModel.exdate).time
            val today = Calendar.getInstance(Locale.KOREAN).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time.time

            return (dDay - today) / (24 * 60 * 60 * 1000)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add("편집")?.setOnMenuItemClickListener {
                updateMenuClick(getItem(adapterPosition))
                true
            }
            menu?.add("삭제")?.setOnMenuItemClickListener {
                deleteMenuClick(getItem(adapterPosition))
                true
            }
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
