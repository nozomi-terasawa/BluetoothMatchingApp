package com.example.bluettoothmatching.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluettoothmatching.data.Post
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.databinding.UserProfileItemBinding

class ItemListAdapter()
    : ListAdapter<Post, ItemListAdapter.ItemViewHolder>(DiffUtilItemCallback){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            UserProfileItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: UserProfileItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        private val fireStore = FireStore()

        fun bind(post: Post) {
            binding.author.text = post.author
            binding.body.text = post.body
            post.image?.getBytes(1024 * 1024)
                ?.addOnSuccessListener { imageData ->
                    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    binding.image.setImageBitmap(bitmap)
                }
            // binding.time.text = post.createTime.toString()

            binding.likeButton.setOnClickListener {
                val uid = post.uid
                val postId = post.postId
                fireStore.addLikedUserToPost(uid, postId)
                binding.likeCount.text = post.likedCount.toString()
            }

        }
    }

    companion object {
        private val DiffUtilItemCallback = object: DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.author == newItem.author
            }
        }
    }
}