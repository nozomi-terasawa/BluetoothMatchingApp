package com.example.bluettoothmatching

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluettoothmatching.databinding.UserProfileItemBinding
import com.example.firestoresample_todo.database.Profile
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ItemListAdapter(private val onItemClicked: (Profile) -> Unit)
    : ListAdapter<Profile, ItemListAdapter.ItemViewHolder>(DiffCallback){


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

        private val storage = Firebase.storage
        var storageRef = storage.reference

        fun bind(profile: Profile) {
            binding.userName.text = profile.name
            binding.userInfo.text = profile.message
            val MAX_SIZE_BYTES: Long = 1024 * 1024
            profile.image?.getBytes(MAX_SIZE_BYTES)
                ?.addOnSuccessListener { imageData ->
                    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    binding.userImage.setImageBitmap(bitmap)
                }
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