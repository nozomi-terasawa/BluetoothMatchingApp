package com.example.bluettoothmatching

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluettoothmatching.databinding.UserProfileItemBinding
import com.example.firestoresample_todo.database.Profile

class ItemListAdapter(private val onItemClicked: (Profile) -> Unit)
    : ListAdapter<Profile, ItemListAdapter.ItemViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            UserProfileItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: UserProfileItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(profile: Profile) {
            binding.userName.text = profile.name
            binding.userInfo.text = profile.message
        }
    }

    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<Profile>() {
            override fun areItemsTheSame(oldItem: Profile, newItem: Profile): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Profile, newItem: Profile): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}