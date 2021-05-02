package com.dlab.sinsungo

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dlab.sinsungo.data.model.Recipe

object CustomBindingAdapter {
    @JvmStatic
    @BindingConversion
    fun convertBooleanToVisibility(visible: Boolean): Int {
        return if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("exDateFormat")
    fun dateFormatting(view: TextView, value: String) {
        view.text = value.replace("-", ".")
    }

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(imageView: ImageView, url: String) {
        Glide.with(imageView.context).load(url).error(R.drawable.image_empty_refrigerator).thumbnail(0.1f)
            .into(imageView)
    }

    @BindingAdapter(*["recipeData", "scrollTop"])
    @JvmStatic
    fun bindRecipe(recyclerView: RecyclerView, recipes: ArrayList<Recipe>?, scrollTop: Boolean) {
        val adapter = recyclerView.adapter as RecipeAdapter

        adapter.submitList(recipes) {
            if (scrollTop) {
                recyclerView.scrollToPosition(0)
            }
        }
    }
}
