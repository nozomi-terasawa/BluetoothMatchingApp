package com.example.firestoresample_todo.database

import com.google.firebase.storage.StorageReference

data class Profile(
    var address: String? = null,
    var name: String? = null,
    var message: String? = null,
    var image: StorageReference? = null
)
