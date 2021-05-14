package com.dlab.sinsungo

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.data.model.Shopping
import com.dlab.sinsungo.databinding.ItemRcviewShoppingListBinding

class ShoppingListAdapter(val deleteClick: (Shopping) -> Unit, val editClick: (Shopping) -> Unit) :
    ListAdapter<Shopping, ShoppingListAdapter.ViewHolder>(ShoppingDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("onCreateViewHolder", "호출")
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRcviewShoppingListBinding>(layoutInflater, viewType, parent, false)
        binding.cvShoppingItem.setBackgroundResource(R.drawable.bg_dialog_white)
        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_rcview_shopping_list
    }

    override fun onBindViewHolder(holder: ShoppingListAdapter.ViewHolder, position: Int) {
        Log.d("onBindViewHolder", "호출")
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemRcviewShoppingListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(shopping: Shopping) {
            Log.d("binding_result", "호출")
            binding.dataModel = shopping
            binding.executePendingBindings() //데이터가 수정되면 즉각 바인딩
            binding.btnDeleteShopping.setOnClickListener {
                deleteClick(getItem(adapterPosition))
            }
            binding.btnEditShopping.setOnClickListener {
                editClick(getItem(adapterPosition))
            }
            binding.cvShoppingItem.setOnClickListener {
                true
            }
        }
    }

    companion object ShoppingDiffUtil : DiffUtil.ItemCallback<Shopping>() {
        override fun areItemsTheSame(oldItem: Shopping, newItem: Shopping): Boolean {
            //각 아이템들의 고유한 값을 비교해야 한다.
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Shopping, newItem: Shopping): Boolean {
            return oldItem == newItem
        }
    }

}

