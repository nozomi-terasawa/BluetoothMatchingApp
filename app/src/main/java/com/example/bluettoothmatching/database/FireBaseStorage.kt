package com.example.bluettoothmatching.database

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class FireBaseStorage {
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val imageRef: StorageReference = storageRef.child(uid.toString())
        val uploadTask = imageRef.putFile(imageUri)

        // 格納
        uploadTask.addOnSuccessListener { }
            .addOnFailureListener { }
    }
}