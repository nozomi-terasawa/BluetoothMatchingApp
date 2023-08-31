package com.example.bluettoothmatching.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluettoothmatching.R
import com.example.bluettoothmatching.data.Post
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.databinding.AdvertiseItemBinding
import com.example.bluettoothmatching.fragment.AdvertiseListFragmentDirections
import com.example.bluettoothmatching.navController

class AdvertiseAdapter
    : ListAdapter<Post, AdvertiseAdapter.ItemViewHolder>(DiffCallback) {
    fun updateList(list: List<Post>) {
        this.submitList(list) {
            list.forEachIndexed { index, value ->
                if (value == getItem(index)) {
                    this.notifyItemChanged(index)
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return  ItemViewHolder(
            AdvertiseItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), parent.context
        )
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
        }
        holder.bind(current)
    }
    class ItemViewHolder(private var binding: AdvertiseItemBinding, private val fragmentContext: Context)
        :RecyclerView.ViewHolder(binding.root) {

        private val fireStore = FireStore()
            fun bind(post: Post) {
                var color = post.color
                try {
                    itemView.setBackgroundColor(Color.parseColor("#$color"))
                } catch (e: java.lang.IllegalArgumentException) {
                    val drawableResourceId = when (color) {
                        "gradient1" -> R.drawable.gradient
                        else -> null
                    }
                    if (drawableResourceId != null) {
                        itemView.setBackgroundResource(drawableResourceId)
                    }
                }
                color = ""

                binding.author.text = post.author
                binding.body.text = post.body
                post.image?.getBytes(1024 * 1024)
                    ?.addOnSuccessListener { imageData ->
                        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                        binding.image.setImageBitmap(bitmap)
                    }
                binding.getAdsButton.setOnClickListener {
                    val message = post.author + "さんの広告を取得しますか？"
                    val builder = AlertDialog.Builder(fragmentContext) // FragmentではrequireContext()を使う
                        .setTitle("")
                        .setMessage(message)
                        .setPositiveButton("はい") { dialog, which ->
                            fireStore.insertAdsForPost(post.uid, post.postId)
                        }
                        .setNegativeButton("いいえ") { dialog, which ->
                            // Noが押された時
                            dialog.dismiss()
                        }
                    builder.show()
                    // fireStore.insertAdsForPost(post.uid, post.postId)
                }

                binding.author.setOnClickListener {
                    val uid = post.uid
                    val action = AdvertiseListFragmentDirections.actionAdvertiseListFragmentToYourProfileDetailFragment(uid)
                    navController.navigate(action)
                }
            }
        }
    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.author == newItem.author || oldItem.body == newItem.body
            }
        }
    }
}