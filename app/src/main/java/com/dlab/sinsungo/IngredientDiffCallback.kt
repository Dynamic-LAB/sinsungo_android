package com.dlab.sinsungo

import androidx.recyclerview.widget.DiffUtil

class IngredientDiffCallback(
    private val oldList: List<IngredientModel>,
    private val newList: List<IngredientModel>
) : DiffUtil.Callback() {
    // RecyclerView의 성능 개선
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id
}
