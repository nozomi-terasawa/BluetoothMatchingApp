package com.example.bluettoothmatching.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluettoothmatching.data.Post
import com.example.bluettoothmatching.databinding.AdvertiseItemBinding

class AdvertiseAdapter()
    : ListAdapter<Post, AdvertiseAdapter.ItemViewHolder>(DiffCallback){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return  ItemViewHolder(
            AdvertiseItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
        }
        holder.bind(current)
    }


    class ItemViewHolder(private var binding: AdvertiseItemBinding)
        :RecyclerView.ViewHolder(binding.root) {

            fun bind(post: Post) {
                binding.author.text = post.author
                binding.body.text = post.body
                post.image?.getBytes(1024 * 1024)
                    ?.addOnSuccessListener { imageData ->
                        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                        binding.image.setImageBitmap(bitmap)
                    }
            }
        }
    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.author == newItem.author
            }
        }
    }
}