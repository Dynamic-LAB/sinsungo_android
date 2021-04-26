package com.dlab.sinsungo

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion

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
}
