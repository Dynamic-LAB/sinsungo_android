package com.dlab.sinsungo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dlab.sinsungo.data.model.User
import com.dlab.sinsungo.databinding.ItemRcviewMemberBinding
import com.dlab.sinsungo.databinding.ItemRcviewUserBinding

class MemberAdapter(private val showDialog: (String) -> Unit) :
    ListAdapter<User, RecyclerView.ViewHolder>(MemberDiffUtil) {
    private val VIEW_TYPE_USER = 0
    private val VIEW_TYPE_MEMBER = 1
    lateinit var master: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding by lazy {
            when (viewType) {
                VIEW_TYPE_USER -> ItemRcviewUserBinding.inflate(layoutInflater, parent, false)
                else -> ItemRcviewMemberBinding.inflate(layoutInflater, parent, false)
            }
        }

        val itemSize = parent.measuredWidth / 5 - binding.root.marginEnd
        val userSize = itemSize * 1.2
        val memberSize = (parent.measuredWidth - userSize) / 4 - binding.root.marginEnd
        val btnSize = itemSize / 2.5

        return when (viewType) {
            VIEW_TYPE_USER -> {
                val itemRcviewUserBinding = binding as ItemRcviewUserBinding
                itemRcviewUserBinding.root.layoutParams.width = userSize.toInt()
                itemRcviewUserBinding.ibUser.layoutParams.width = btnSize.toInt()
                itemRcviewUserBinding.ibUser.layoutParams.height = btnSize.toInt()
                UserViewHolder(itemRcviewUserBinding)
            }
            else -> {
                val itemRcviewMemberBinding = binding as ItemRcviewMemberBinding
                itemRcviewMemberBinding.root.layoutParams.width = memberSize.toInt()
                itemRcviewMemberBinding.ibRemove.layoutParams.width = (btnSize / 1.2).toInt()
                itemRcviewMemberBinding.ibRemove.layoutParams.height = (btnSize / 1.2).toInt()
                MemberViewHolder(itemRcviewMemberBinding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).userId) {
            GlobalApplication.prefs.getString("userId") -> VIEW_TYPE_USER
            else -> VIEW_TYPE_MEMBER
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MemberViewHolder) {
            holder.bind(getItem(position))
        } else if (holder is UserViewHolder) {
            holder.bind(getItem(position))
        }
    }

    inner class MemberViewHolder(private val binding: ItemRcviewMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            if (user.userId == master) {
                binding.ibRemove.setImageResource(R.drawable.ic_master_circle)
                binding.ibRemove.isEnabled = false
                binding.ibRemove.visibility = View.VISIBLE
            }

            if (master == GlobalApplication.prefs.getString("userId")) {
                binding.ibRemove.visibility = View.VISIBLE
            }

            if (adapterPosition == currentList.size - 1) {
                (binding.root.layoutParams as ViewGroup.MarginLayoutParams).marginEnd = 0
            }

            binding.member = user
            binding.executePendingBindings() //데이터가 수정되면 즉각 바인딩

            binding.ibRemove.setOnClickListener {
                showDialog(adapterPosition.toString())
            }
        }
    }

    inner class UserViewHolder(private val binding: ItemRcviewUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            if (user.userId == master) {
                binding.ibUser.setImageResource(R.drawable.ic_master_circle)
                binding.ibUser.isEnabled = false
                binding.ibUser.visibility = View.VISIBLE
            }

            if (adapterPosition == currentList.size - 1) {
                (binding.root.layoutParams as ViewGroup.MarginLayoutParams).marginEnd = 0
            }

            binding.member = user
            binding.executePendingBindings() //데이터가 수정되면 즉각 바인딩
        }
    }

    companion object MemberDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            //각 아이템들의 고유한 값을 비교해야 한다.
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
