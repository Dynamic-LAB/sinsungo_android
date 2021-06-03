package com.dlab.sinsungo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.R
import com.dlab.sinsungo.data.model.Diet
import com.dlab.sinsungo.databinding.ItemRcviewDietBinding

class DietListAdapter(
    val deleteClick: ((Diet) -> Unit)? = null,
    val editClick: ((Diet) -> Unit)? = null,
    val itemClick: ((Diet) -> Unit)? = null
) :
    ListAdapter<Diet, DietListAdapter.ViewHolder>(DietDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRcviewDietBinding>(layoutInflater, viewType, parent, false)
        binding.cvDietItem.setBackgroundResource(R.drawable.bg_dialog_white)
        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_rcview_diet
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemRcviewDietBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(diet: Diet) {
            binding.dataModel = diet

            binding.executePendingBindings() //데이터가 수정되면 즉각 바인딩
            binding.btnDeleteDiet.setOnClickListener {
                deleteClick?.invoke(getItem(adapterPosition))
            }
            binding.btnEditDiet.setOnClickListener {
                editClick?.invoke(getItem(adapterPosition))
            }
            binding.cvDietItem.setOnClickListener {
                itemClick?.invoke(getItem(adapterPosition))
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
