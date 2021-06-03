package com.dlab.sinsungo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.data.model.NotificationModel
import com.dlab.sinsungo.databinding.ItemRcviewNotificationBinding

class NotificationListAdapter :
    ListAdapter<NotificationModel, NotificationListAdapter.NotificationViewHolder>(NotificationDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemRcviewNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(val binding: ItemRcviewNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notificationModel: NotificationModel) {
            binding.data = notificationModel
        }
    }

    companion object NotificationDiffUtil : DiffUtil.ItemCallback<NotificationModel>() {
        override fun areItemsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean {
            return oldItem == newItem
        }
    }
}
