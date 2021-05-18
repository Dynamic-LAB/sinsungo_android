package com.dlab.sinsungo

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.data.model.Diet
import com.dlab.sinsungo.databinding.ItemRcviewDietBinding

class DietListAdapter(val deleteClick: (Diet) -> Unit, val editClick: (Diet) -> Unit) :
    ListAdapter<Diet, DietListAdapter.ViewHolder>(DietDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("onCreateViewHolder", "호출")
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRcviewDietBinding>(layoutInflater, viewType, parent, false)
        binding.cvDietItem.setBackgroundResource(R.drawable.bg_dialog_white)
        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_rcview_diet
    }

    override fun onBindViewHolder(holder: DietListAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemRcviewDietBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(diet: Diet) {
            binding.dataModel = diet
            binding.executePendingBindings() //데이터가 수정되면 즉각 바인딩
            binding.btnDeleteDiet.setOnClickListener {
                deleteClick(getItem(adapterPosition))
            }
            binding.btnEditDiet.setOnClickListener {
                editClick(getItem(adapterPosition))
            }
            binding.cvDietItem.setOnClickListener {
                true
            }
        }

    }

    companion object DietDiffUtil : DiffUtil.ItemCallback<Diet>() {
        override fun areItemsTheSame(oldItem: Diet, newItem: Diet): Boolean {
            //각 아이템들의 고유한 값을 비교해야 한다.
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Diet, newItem: Diet): Boolean {
            return oldItem == newItem
        }
    }

}
