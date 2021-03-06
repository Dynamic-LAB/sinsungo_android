package com.dlab.sinsungo.adapters

import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.R
import com.dlab.sinsungo.data.model.IngredientModel
import com.dlab.sinsungo.databinding.ItemRcviewReceiptOcrBinding
import java.text.SimpleDateFormat
import java.util.*

class ReceiptListAdapter(
    val delete: (IngredientModel) -> Unit,
    val update: (String, String, IngredientModel) -> Unit
) : ListAdapter<IngredientModel, ReceiptListAdapter.ReceiptViewHolder>(ReceiptDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val binding = ItemRcviewReceiptOcrBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ReceiptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReceiptViewHolder(val binding: ItemRcviewReceiptOcrBinding) : RecyclerView.ViewHolder(binding.root) {
        private val mCalendar = Calendar.getInstance()

        private val mOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, month)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        private val mOnClickOpenDatePicker = View.OnClickListener { view: View ->
            val datePicker = DatePickerDialog(
                view.context,
                mOnDateSetListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        private fun updateLabel() {
            val mDateFormat = "yyyy-MM-dd"
            val mSimpleDateFormat = SimpleDateFormat(mDateFormat, Locale.KOREA)
            val mDateString = mSimpleDateFormat.format(mCalendar.time)

            binding.tvExdateInput.text = mDateString
            update("exDate", mDateString, getItem(adapterPosition))
        }

        fun bind(ingredientModel: IngredientModel) {
            binding.data = ingredientModel

            binding.btnOpenCategory.setOnClickListener {
                showRefCategories(binding.tvRefCategory, R.menu.menu_ref_categories)
            }

            binding.btnOpenCountType.setOnClickListener {
                showCountTypes(binding.tvCountType, R.menu.menu_count_type)
            }

            binding.btnOpenExdateType.setOnClickListener {
                showExdateTypes(binding.tvExdateType, R.menu.menu_exdate_type)
            }

            binding.tvExdateInput.setOnClickListener(mOnClickOpenDatePicker)

            binding.btnDropItem.setOnClickListener {
                deleteItem()
            }

            binding.etCount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().toIntOrNull() == null) {
                        update("count", "0", getItem(adapterPosition))
                    } else {
                        update("count", s.toString(), getItem(adapterPosition))
                    }
                }
            })
        }

        private fun deleteItem() {
            delete(getItem(adapterPosition))
        }

        private fun showRefCategories(view: View, @MenuRes menuRes: Int) {
            val refCategoryPopup = PopupMenu(view.context, view)
            refCategoryPopup.menuInflater.inflate(menuRes, refCategoryPopup.menu)

            refCategoryPopup.setOnMenuItemClickListener { menuItem: MenuItem ->
                binding.tvRefCategory.text = menuItem.title.toString()
                update("refCategory", menuItem.title.toString(), getItem(adapterPosition))
                // getItem(adapterPosition).refCategory = menuItem.title.toString()
                true
            }

            refCategoryPopup.show()
        }

        private fun showCountTypes(view: View, @MenuRes menuRes: Int) {
            val countTypePopup = PopupMenu(view.context, view)
            countTypePopup.menuInflater.inflate(menuRes, countTypePopup.menu)

            countTypePopup.setOnMenuItemClickListener { menuItem: MenuItem ->
                binding.tvCountType.text = menuItem.title.toString()
                update("countType", menuItem.title.toString(), getItem(adapterPosition))
                // getItem(adapterPosition).countType = menuItem.title.toString()
                true
            }

            countTypePopup.show()
        }

        private fun showExdateTypes(view: View, @MenuRes menuRes: Int) {
            val exdateTypePopup = PopupMenu(view.context, view)
            exdateTypePopup.menuInflater.inflate(menuRes, exdateTypePopup.menu)

            exdateTypePopup.setOnMenuItemClickListener { menuItem: MenuItem ->
                binding.tvExdateType.text = menuItem.title.toString()
                update("exDateType", menuItem.title.toString(), getItem(adapterPosition))
                // getItem(adapterPosition).exDateType = menuItem.title.toString()
                true
            }

            exdateTypePopup.show()
        }
    }

    companion object ReceiptDiffUtil : DiffUtil.ItemCallback<IngredientModel>() {
        override fun areContentsTheSame(oldItem: IngredientModel, newItem: IngredientModel): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: IngredientModel, newItem: IngredientModel): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
