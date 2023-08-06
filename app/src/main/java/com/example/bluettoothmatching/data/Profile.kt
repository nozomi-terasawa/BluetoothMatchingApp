package com.example.bluettoothmatching.data

import com.google.firebase.Timestamp

data class Profile(
    val macAddress: String,
    val name: String,
    val introduction: String,
    val createTime: Timestamp,
    val likePostCount: Int,
    val point: Int
)
