package com.example.bluettoothmatching.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluettoothmatching.data.Post
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.databinding.RepostAdsItemBinding
import com.example.bluettoothmatching.databinding.UserProfileItemBinding


class ItemListAdapter()
    : ListAdapter<Post, RecyclerView.ViewHolder>(DiffUtilItemCallback){

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.type == 1) 1 else 2
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return ItemViewHolder(UserProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            return AdsItemViewHolder(RepostAdsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = getItem(position)
        when (holder) {
            is ItemViewHolder -> holder.bind(post)
            is AdsItemViewHolder -> holder.bind(post)
            else -> throw IllegalArgumentException("Unknown ViewHolder type")
        }
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

    class AdsItemViewHolder(private var binding: RepostAdsItemBinding)
        :RecyclerView.ViewHolder(binding.root) {

        private val fireStore = FireStore()
        fun bind(post: Post) {
            binding.author.text = post.author
            //binding.originalPoster.text =
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