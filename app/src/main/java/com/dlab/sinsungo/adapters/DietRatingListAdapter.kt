package com.dlab.sinsungo.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.R
import com.dlab.sinsungo.data.model.IngredientModel
import com.dlab.sinsungo.data.model.Rating
import com.dlab.sinsungo.databinding.ItemRcviewDietRatingBinding

class DietRatingListAdapter :
    ListAdapter<Rating, DietRatingListAdapter.ViewHolder>(DietRatingDiffUtil) {

    var ratingDietList: MutableMap<String, Float> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRcviewDietRatingBinding>(layoutInflater, viewType, parent, false)
        val holder = ViewHolder(binding)
        binding.cvDietRatingItem.setBackgroundResource(R.drawable.bg_dialog_white)
        Log.d("onCreateViewHolder", "")
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_rcview_diet_rating
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("onBindViewHolder", "")
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemRcviewDietRatingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Rating) {
            binding.dataModel = data
            binding.ratingDiet.setOnRatingBarChangeListener { _, rating, _ ->
                binding.dataModel = Rating(data.recipeName, rating)
                ratingDietList[data.recipeName] = rating
                Log.d("ratingDietList", ratingDietList.toString())
            }
            Log.d("ratingDietList1", ratingDietList.toString())
            ratingDietList[data.recipeName] = data.rating
            binding.executePendingBindings() //데이터가 수정되면 즉각 바인딩
        }
    }

    companion object DietRatingDiffUtil : DiffUtil.ItemCallback<Rating>() {
        override fun areItemsTheSame(oldItem: Rating, newItem: Rating): Boolean {
            //각 아이템들의 고유한 값을 비교해야 한다.
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Rating, newItem: Rating): Boolean {
            return oldItem.recipeName == newItem.recipeName
        }
    }
}

