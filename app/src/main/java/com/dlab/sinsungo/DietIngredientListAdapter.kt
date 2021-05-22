package com.dlab.sinsungo

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.databinding.ItemRcviewDietIngredientBinding

class DietIngredientListAdapter :
    ListAdapter<IngredientModel, DietIngredientListAdapter.ViewHolder>(DietIngredientDiffUtil) {

    var tracker: SelectionTracker<Long>? = null
    val ingredientList = mutableListOf<IngredientModel>()

    init {
        setHasStableIds(true)
    }

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
        tracker?.let { holder.bind(getItem(position), it.isSelected(position.toLong())) }
    }

    override fun getItemId(position: Int): Long = position.toLong()


    inner class ViewHolder(val binding: ItemRcviewDietIngredientBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredientModel: IngredientModel, isActivated: Boolean) {
            binding.dataModel = ingredientModel
            binding.btnCheckIngredient.isActivated = isActivated
            if (isActivated) {
                ingredientList.add(ingredientModel)
            } else {
                ingredientList.remove(ingredientModel)
            }
            Log.d("ingredientList", ingredientList.toString())
            binding.executePendingBindings() //데이터가 수정되면 즉각 바인딩
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long = itemId
                override fun inSelectionHotspot(e: MotionEvent): Boolean = true
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

class MyItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as DietIngredientListAdapter.ViewHolder)
                .getItemDetails()
        }
        return null
    }
}

