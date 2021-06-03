package com.dlab.sinsungo.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.data.model.IngredientModel
import com.dlab.sinsungo.R
import com.dlab.sinsungo.databinding.ItemRcviewDietIngredientBinding

class DietIngredientListAdapter(
    val toUse: (IngredientModel) -> Unit,
    val toUnUse: (IngredientModel) -> Unit,
    private val useIngredient: List<IngredientModel>?
) :
    ListAdapter<IngredientModel, DietIngredientListAdapter.ViewHolder>(DietIngredientDiffUtil) {

    val ingredientList = mutableListOf<IngredientModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRcviewDietIngredientBinding>(layoutInflater, viewType, parent, false)
        val holder = ViewHolder(binding)
        binding.cvIngredientDietItem.setBackgroundResource(R.drawable.bg_dialog_white)
        Log.d("onCreateViewHolder", "")
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_rcview_diet_ingredient
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("onBindViewHolder", "")
        if (useIngredient != null) {
            holder.bind(getItem(position), getItem(position) in useIngredient)
        } else {
            holder.bind(getItem(position), false)
        }
    }

    inner class ViewHolder(val binding: ItemRcviewDietIngredientBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredientModel: IngredientModel, isActivated: Boolean) {
            binding.dataModel = ingredientModel
            binding.btnCheckIngredient.isActivated = isActivated
            Log.d("bind", ingredientModel.toString())
            binding.btnCheckIngredient.setOnClickListener {
                binding.btnCheckIngredient.isActivated = !binding.btnCheckIngredient.isActivated
                if (binding.btnCheckIngredient.isActivated) {
                    toUse(ingredientModel)
                    ingredientList.add(ingredientModel)
                } else {
                    toUnUse(ingredientModel)
                    ingredientList.remove(ingredientModel)
                }
            }
            if (binding.btnCheckIngredient.isActivated) {
                ingredientList.add(ingredientModel)
            } else {
                ingredientList.remove(ingredientModel)
            }
            binding.executePendingBindings() //데이터가 수정되면 즉각 바인딩
        }
    }

    companion object DietIngredientDiffUtil : DiffUtil.ItemCallback<IngredientModel>() {
        override fun areItemsTheSame(oldItem: IngredientModel, newItem: IngredientModel): Boolean {
            //각 아이템들의 고유한 값을 비교해야 한다.
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: IngredientModel, newItem: IngredientModel): Boolean {
            return oldItem.id == newItem.id
        }
    }
}

