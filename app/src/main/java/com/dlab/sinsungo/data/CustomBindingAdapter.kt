package com.dlab.sinsungo.data

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dlab.sinsungo.R
import com.dlab.sinsungo.adapters.*
import com.dlab.sinsungo.data.model.*
import kotlin.math.abs

object CustomBindingAdapter {
    @BindingConversion
    @JvmStatic
    fun convertBooleanToVisibility(visible: Boolean): Int {
        return if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("tvRemain", "tvExdateType")
    @JvmStatic
    fun setExDateMessage(view: TextView, value: Long, type: String) {
        if (type == "유통기한") {
            if (value >= 0) view.text = "%s까지 %d일 남았습니다.".format(type, value)
            else view.text = "%s에서 %d일 지났습니다.".format(type, abs(value))
        } else view.text = "%s에서 %d일 지났습니다.".format(type, abs(value))
    }

    @BindingAdapter("remain", "exdateType", "drawableGood", "drawableBad")
    @JvmStatic
    fun setProgressBarState(
        view: ProgressBar,
        value: Long,
        type: String,
        drawableGood: Drawable,
        drawableBad: Drawable,
    ) {
        if (type == "유통기한") {
            when {
                value >= 10 -> view.progress = 25
                value in 5..9 -> view.progress = 50
                value in 3..4 -> view.progress = 75
                value in 1..2 -> view.progress = 90
                value <= 0 -> view.progress = 100
            }
        } else {
            when {
                value == 0L -> view.progress = 5
                value in -4..-1 -> view.progress = 25
                value in -9..-5 -> view.progress = 50
                value in -14..-10 -> view.progress = 75
                value <= -15 -> view.progress = 90
            }
        }
        if (view.progress > 50) view.progressDrawable = drawableBad
        else view.progressDrawable = drawableGood
    }

    @BindingAdapter("ingredientData", "category")
    @JvmStatic
    fun bindIngredient(recyclerView: RecyclerView, ingredients: List<IngredientModel>?, category: String) {
        val adapter = recyclerView.adapter as IngredientListAdapter

        val data = ingredients?.filter { it.refCategory == category }

        adapter.submitList(data)
    }

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(imageView: ImageView, url: String) {
        Glide.with(imageView.context).load(url).error(R.drawable.img_empty_thumbnail).thumbnail(0.1f)
            .into(imageView)
    }

    @BindingAdapter("recipeData", "scrollTop")
    @JvmStatic
    fun bindRecipe(recyclerView: RecyclerView, recipes: ArrayList<Recipe>?, scrollTop: Boolean) {
        val adapter = recyclerView.adapter as RecipeAdapter

        adapter.submitList(recipes) {
            if (scrollTop) {
                recyclerView.scrollToPosition(0)
            }
        }
    }

    @BindingAdapter("recommendData")
    @JvmStatic
    fun bindRecommend(viewPager2: ViewPager2, recipes: ArrayList<Recipe>?) {
        val adapter = viewPager2.adapter as RecipeAdapter

        adapter.submitList(recipes)
    }

    @BindingAdapter("shoppingData")
    @JvmStatic
    fun bindShopping(recyclerView: RecyclerView, shopping: List<Shopping>?) {
        val adapter = recyclerView.adapter as ShoppingListAdapter
        adapter.submitList(shopping?.toMutableList())
    }

    @BindingAdapter("shoppingItem")
    @JvmStatic
    fun bindShoppingItem(textView: TextView, value: String?) {
        if (value == null || value == "") {
            textView.visibility = View.GONE
        }
    }

    @BindingAdapter("span_text", "span_color")
    @JvmStatic
    fun setDialogTitle(textView: TextView, value: String, color: Int) {
        val spannableString = SpannableString(value)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            7,
            9,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableString
    }

    @BindingAdapter("members", "master")
    @JvmStatic
    fun bindMember(recyclerView: RecyclerView, members: ArrayList<User>?, master: String?) {
        val adapter = recyclerView.adapter as MemberAdapter
        master?.let { adapter.master = it }

        adapter.submitList(members)
    }

    @BindingAdapter("receiptData")
    @JvmStatic
    fun bindReceipt(recyclerView: RecyclerView, ingredients: List<IngredientModel>?) {
        val adapter = recyclerView.adapter as ReceiptListAdapter
        adapter.submitList(ingredients?.toMutableList())
    }

    @BindingAdapter("tvDietDate")
    @JvmStatic
    fun setDietDate(view: TextView, value: String) {
        view.text = value
    }

    @BindingAdapter("tvDietMenus")
    @JvmStatic
    fun setDietMenus(view: TextView, value: List<String>) {
        val diets = value.filterNotNull()
        view.text = diets.joinToString(",")
    }

    @BindingAdapter("tvDietIngredient")
    @JvmStatic
    fun setDietIngredient(view: TextView, value: List<IngredientModel>?) {
        if (value == null || value.isEmpty() || value[0].id == null) {
            view.text = view.resources.getString(R.string.diet_no_ingredient)
            view.setTextColor(
                ResourcesCompat.getColor(
                    view.resources,
                    R.color.free_speech_red,
                    view.resources.newTheme()
                )
            )
        } else {
            val valueString: List<String> = value.map { it.name }
            view.text = valueString.joinToString(",")
        }
    }

    @BindingAdapter("tvDietName")
    @JvmStatic
    fun setDietIngredientGone(view: TextView, value: List<IngredientModel>?) {
        if (value == null || value.isEmpty() || value[0].id == null) {
            view.visibility = View.GONE
        }
    }

    @BindingAdapter("dietData")
    @JvmStatic
    fun bindDiet(recyclerView: RecyclerView, diet: List<Diet>?) {
        val adapter = recyclerView.adapter as DietListAdapter
        adapter.submitList(diet?.toMutableList())
    }

    @BindingAdapter("dietRatingData")
    @JvmStatic
    fun bindRatingDiet(recyclerView: RecyclerView, recipes: List<String?>?) {
        val adapter = recyclerView.adapter as DietRatingListAdapter
        val ratings = ArrayList<Rating>()
        Log.d("customBinding", recipes.toString())
        if (recipes != null) {
            for (recipe in recipes) {
                if (recipe != "null") {
                    recipe?.let { Rating(it, 0.0f) }?.let { ratings.add(it) }
                }
            }
        }
        adapter.submitList(ratings.toMutableList())
    }

    @BindingAdapter("unUseIngredients", "useIngredients")
    @JvmStatic
    fun bindIngredientDiet(
        recyclerView: RecyclerView,
        unUseIngredients: List<IngredientModel>?,
        useIngredients: List<IngredientModel>?
    ) {
        Log.d("bindIngredientDiet", "")
        val adapter = recyclerView.adapter as DietIngredientListAdapter

        val ingredients = mutableListOf<IngredientModel>()

        useIngredients?.let { ingredients.addAll(it) }
        unUseIngredients?.let { ingredients.addAll(it) }

        adapter.submitList(ingredients)
    }

    @BindingAdapter("notices")
    @JvmStatic
    fun bindNotice(recyclerView: RecyclerView, notices: List<Notice>?) {
        val adapter = recyclerView.adapter as NoticeAdapter

        adapter.submitList(notices)
    }

    @BindingAdapter("notifications")
    @JvmStatic
    fun bindNotification(recyclerView: RecyclerView, notifications: List<NotificationModel>?) {
        val adapter = recyclerView.adapter as NotificationListAdapter

        adapter.submitList(notifications)
    }

    @BindingAdapter("notification_span_text", "notification_span_color")
    @JvmStatic
    fun setNotificationMessage(textView: TextView, value: String, color: Int) {
        val spannableString = SpannableString(value)
        spannableString.apply {
            setSpan(
                ForegroundColorSpan(color),
                6,
                7,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                StyleSpan(Typeface.BOLD),
                6,
                7,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.text = spannableString
    }
}
