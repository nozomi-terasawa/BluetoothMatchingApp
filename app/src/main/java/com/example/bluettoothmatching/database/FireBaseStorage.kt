package com.example.bluettoothmatching.database

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.bluettoothmatching.databinding.UserProfileItemBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class FireBaseStorage {

    private val storage = Firebase.storage
    var storageRef = storage.reference

    fun uploadImageToFirebaseStorage(imageUri: Uri) {

        var imageRef: StorageReference? = storageRef.child(uid.toString())

        val uploadTask = imageRef?.putFile(imageUri)

        // 格納
        uploadTask?.addOnSuccessListener {
            Log.d("image", "成功")
        }
            ?.addOnFailureListener {
                Log.d("image", "失敗")
            }
    }

    fun getImage(binding: UserProfileItemBinding) {
        val MAX_SIZE_BYTES: Long = 1024 * 1024
        val userImageRef = storageRef.child(uid.toString())
        userImageRef.getBytes(MAX_SIZE_BYTES)
            .addOnSuccessListener { imageData ->
                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size).toString()
                // binding.userImage.setImageBitmap(bitmap)
            }
    }
}