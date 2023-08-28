package com.example.bluettoothmatching.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluettoothmatching.R
import com.example.bluettoothmatching.data.Post
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.databinding.RepostAdsItemBinding
import com.example.bluettoothmatching.databinding.UserProfileItemBinding

class ItemListAdapter
    : ListAdapter<Post, RecyclerView.ViewHolder>(DiffUtilItemCallback) {
    fun updateList(list: List<Post>) {
        this.submitList(list) {
            list.forEachIndexed { index, value ->
                if (value == getItem(index)) {
                    this.notifyItemChanged(index)
                }
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.type == 1) 1 else 2
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return ItemViewHolder(UserProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), parent.context)
        } else {
            return AdsItemViewHolder(RepostAdsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), parent.context)
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
    class ItemViewHolder(private var binding: UserProfileItemBinding, private val context: Context)
        : RecyclerView.ViewHolder(binding.root) {

        private val fireStore = FireStore()
        fun bind(post: Post) {
            var color = post.color
            try {
                itemView.setBackgroundColor(Color.parseColor("#$color"))
            } catch (e: java.lang.IllegalArgumentException) {
                val drawableResourceId = when (color) {
                    "hosizora" -> R.drawable.hosizora
                    else -> 0
                }
                if (drawableResourceId != 0) {
                    val drawable = ContextCompat.getDrawable(context, drawableResourceId)
                    if (drawable != null) {
                        binding.backgroundImageView.setImageDrawable(drawable)

                    } else {
                        // Drawable の取得に失敗した場合の処理
                        binding.backgroundImageView.setImageDrawable(null)
                    }

                }
                color = ""
            }

            binding.author.text = post.author
            binding.body.text = post.body
            post.image?.getBytes(1024 * 1024)
                ?.addOnSuccessListener { imageData ->
                    var bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    binding.image.setImageBitmap(bitmap)
                    bitmap = null
                }
                    // todo これがないと画像が増える
                ?.addOnFailureListener {
                    // 画像の取得に失敗した場合の処理
                    binding.image.setImageBitmap(null)
                }
            binding.likeCount.text = post.likedCount.toString()
            binding.likeButton.setOnClickListener {
                val uid = post.uid
                val postId = post.postId
                fireStore.addLikedUserToPost(uid, postId, binding)
            }
        }
    }
    class AdsItemViewHolder(private var binding: RepostAdsItemBinding, private val context: Context)
        :RecyclerView.ViewHolder(binding.root) {

        private val fireStore = FireStore()
        fun bind(post: Post) {
             binding.author.text = post.author
            binding.originalPoster.text = context.getString(R.string.original_poster, post.otherAuthor)
            binding.body.text = post.body
            post.image?.getBytes(1024 * 1024)
                ?.addOnSuccessListener { imageData ->
                    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    binding.image.setImageBitmap(bitmap)
                }
            binding.likeCount.text = post.likedCount.toString()
            binding.likeButton.setOnClickListener {
                val uid = post.uid
                val postId = post.postId
                fireStore.addLikedUserToPost2(uid, postId, binding)
            }
        }
    }
    companion object {
        private val DiffUtilItemCallback = object: DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.author == newItem.author || oldItem.body == newItem.body || oldItem.likedCount == newItem.likedCount
            }
        }
    }
}