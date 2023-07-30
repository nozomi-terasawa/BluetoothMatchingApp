package com.example.bluettoothmatching.data

import com.google.firebase.storage.StorageReference

data class Post(
    val uid: String,
    val postId: String,
    val body: String,
    val image: StorageReference? = null,
    val author: String,
    //val createTime: Timestamp,
    val likedCount: Int, // いいねされた数
    val type: Int,
)
