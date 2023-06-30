package com.example.bluettoothmatching.database

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class FireBaseStorage {
    val storage = Firebase.storage

    var storageRef = storage.reference
    var imageRef: StorageReference? = storageRef.child("images")

    // 格納
    var spaceRef = storageRef.child("/path/to/images/my_home.svg")

    var file = Uri.fromFile(File("Path/to/images/my_home.ping"))
    val riversRef = storageRef.child("images/${file.lastPathSegment}")

/*
    var uploadTask = riversRef.putFile(file)
    uploadTask.addOnFailureListener {
        // Handle unsuccessful uploads
    }.addOnSuccessListener { taskSnapshot ->
        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
        // ...
    }

 */

}