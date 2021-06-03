package com.dlab.sinsungo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.R
import com.dlab.sinsungo.data.model.Notice
import com.dlab.sinsungo.databinding.ItemRcviewNoticeBinding

class NoticeAdapter(val todoItemClick: (Notice) -> Unit) :
    ListAdapter<Notice, NoticeAdapter.ViewHolder>(NoticeDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRcviewNoticeBinding>(layoutInflater, viewType, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_rcview_notice
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRcviewNoticeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notice: Notice) {
            binding.notice = notice.copy(date = notice.date.split(' ')[0])
            binding.executePendingBindings() //데이터가 수정되면 즉각 바인딩

            binding.root.setOnClickListener {
                todoItemClick(notice)
            }
        }
    }

    companion object NoticeDiffUtil : DiffUtil.ItemCallback<Notice>() {
        override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean {
            //각 아이템들의 고유한 값을 비교해야 한다.
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Notice, newItem: Notice): Boolean {
            return oldItem == newItem
        }
    }
}
