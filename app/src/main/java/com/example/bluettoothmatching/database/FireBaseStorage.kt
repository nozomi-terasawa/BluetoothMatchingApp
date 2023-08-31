package com.example.bluettoothmatching.database

import android.net.Uri
import com.example.bluettoothmatching.fragment.CreatePostFragmentDirections
import com.example.bluettoothmatching.navController
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class FireBaseStorage {

    private val storage = Firebase.storage
    var storageRef = storage.reference
    fun uploadImageToFirebaseStorage(imageUri: Uri, imageName: String) {
        val imageRef: StorageReference = storageRef.child(imageName)
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                val action = CreatePostFragmentDirections.actionCreatePostFragment2ToProfileListFragment()
                navController.navigate(action)
            }
    }
}