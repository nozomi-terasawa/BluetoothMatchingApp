package com.example.bluettoothmatching

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluettoothmatching.database.FireBaseStorage
import com.example.bluettoothmatching.database.NewProfile
import com.example.bluettoothmatching.databinding.UserProfileItemBinding

class ItemListAdapter(private val onItemClicked: (NewProfile) -> Unit)
    : ListAdapter<NewProfile, ItemListAdapter.ItemViewHolder>(DiffCallback){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            UserProfileItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
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

        private val storage = FireBaseStorage()

        fun bind(newProfile: NewProfile) {
            binding.userName.text = newProfile.name
            binding.userInfo.text = newProfile.message
            //storage.getImage(binding) // 戻り値として受けとる画像
            // binding.userImage.setImageBitmap(bitmap)

        }
    }

    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<NewProfile>() {
            override fun areItemsTheSame(oldItem: NewProfile, newItem: NewProfile): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: NewProfile, newItem: NewProfile): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}