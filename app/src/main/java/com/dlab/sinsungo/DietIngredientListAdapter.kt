package com.dlab.sinsungo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.databinding.ItemRcviewDietIngredientBinding

class DietIngredientListAdapter(val addClick: (IngredientModel) -> Unit, val removeClick: (IngredientModel) -> Unit) :
    ListAdapter<IngredientModel, DietIngredientListAdapter.ViewHolder>(DietIngredientDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRcviewDietIngredientBinding>(layoutInflater, viewType, parent, false)
        binding.cvIngredientDietItem.setBackgroundResource(R.drawable.bg_dialog_white)

        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_rcview_diet_ingredient
    }

    override fun onBindViewHolder(holder: DietIngredientListAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemRcviewDietIngredientBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredientModel: IngredientModel) {
            binding.dataModel = ingredientModel
            binding.executePendingBindings() //데이터가 수정되면 즉각 바인딩
        }
    }

    companion object DietIngredientDiffUtil : DiffUtil.ItemCallback<IngredientModel>() {
        override fun areItemsTheSame(oldItem: IngredientModel, newItem: IngredientModel): Boolean {
            //각 아이템들의 고유한 값을 비교해야 한다.
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: IngredientModel, newItem: IngredientModel): Boolean {
            return oldItem == newItem
        }
    }

}

