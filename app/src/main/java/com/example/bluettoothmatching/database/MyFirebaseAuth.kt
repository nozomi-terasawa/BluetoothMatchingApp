package com.example.bluettoothmatching.database

import com.google.firebase.auth.FirebaseAuth

private var _uid: String? = null // uidの初期化
val uid get() = _uid

class MyFirebaseAuth {

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
}