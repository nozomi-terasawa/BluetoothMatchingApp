package com.example.bluettoothmatching.database

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


private var _uid: String? = null // uidの初期化
val uid get() = _uid

class MyFirebaseAuth {

    private val db = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()

    // 新規登録
    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    _uid = auth.currentUser?.uid // 現在ログインしているユーザーのUIDを取得

                } else { }
            }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    _uid = auth.currentUser?.uid // 現在ログインしているユーザーのUIDを取得

                }
            }
    }

    fun upDate(userAddress: String, userName: String, userInfo: String) {
        Log.d("nozomi", "true")
        val collectionRef = db.collection("users")
        if (uid != null) {
            val documentRef = collectionRef.document(uid!!) // todo uidがnull
            val updates = hashMapOf<String, Any>(
                "address" to "58:C6:F0:5C:85:68",
                "message" to userInfo,
                "name" to userName
            )
            documentRef.update(updates)
                .addOnSuccessListener { Log.d("nozomi", "更新しました") }
                .addOnFailureListener { Log.d("nozomi", "更新に失敗しました") }

        } else {
            Log.d("nozomi", "失敗しています")
        }
    }
}