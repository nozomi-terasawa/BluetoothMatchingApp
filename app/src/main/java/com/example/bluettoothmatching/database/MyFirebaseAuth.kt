package com.example.bluettoothmatching.database

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


var _uid: String? = null // uidの初期化
val uid get() = _uid

class MyFirebaseAuth {
    private val db = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()

    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                    _uid = auth.currentUser?.uid // 現在ログインしているユーザーのUIDを取得
            }
    }

    fun upData(userAddress: String, userName: String, userInfo: String) {
//        val collectionRef = db.collection("users")
        if (uid != null) {
            val documentRef = db.collection("users")
                .document(uid!!) // todo uidがnull
            val updates = hashMapOf<String, Any>(
                "address" to userAddress,
                "message" to userInfo,
                "name" to userName
            )
            documentRef.update(updates)
                .addOnSuccessListener { Log.d("nozomi", "更新しました") }
                .addOnFailureListener { Log.d("nozomi", "更新に失敗しました") }

        } else {
            Log.d("nozomi", "uidが未参照です")
        }
    }
}