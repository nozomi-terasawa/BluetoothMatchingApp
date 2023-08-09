package com.example.bluettoothmatching.database

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.bluettoothmatching.R
import com.example.bluettoothmatching.adapter.AdvertiseAdapter
import com.example.bluettoothmatching.adapter.ItemListAdapter
import com.example.bluettoothmatching.bluetooth.tmpList
import com.example.bluettoothmatching.data.Post
import com.example.bluettoothmatching.databinding.FragmentProfileListBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

var imageRef: String? = null

class FireStore {
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private var likedCount: Int = 0

    private val userDocumentRef = db.collection("users")
    private val userRef = userDocumentRef.document(uid!!)

    fun createProfile(macAddress: String, name: String, introduction: String) {
        userRef
            .set(
                mapOf(
                    "macAddress" to macAddress,
                    "name" to name,
                    "introduction" to introduction,
                    "point" to 0,
                    // "createTime" to FieldValue.serverTimestamp(),
                    // "likePostCount" to 0,
                )
            )
    }

    // 投稿
    fun post(body: String) {
        val postRef = userRef.collection("post").document() // ドキュメントIDを自動生成
        imageRef = postRef.id
        postRef.set(
            mapOf(
                "body" to body,
                // "author" to userRef,
                // "createTime" to FieldValue.serverTimestamp(),
                "likeCount" to 0, // いいねされた数
                "type" to 1
            )
        )
    }

    fun advertise(body: String) {
        val advertiseRef = userRef.collection("advertise").document()
        imageRef = advertiseRef.id
        advertiseRef.set(
            mapOf(
                "body" to body,
                // "author" to userRef,
                // "createTime" to FieldValue.serverTimestamp(),
            )
        )
    }



    fun addLikedUserToPost(userId: String, postId: String) {
        Log.d("like", "true")
        val userRef = userDocumentRef.document(userId)
        val postRef = userRef.collection("post").document(postId)

        // サブコレクションを追加
        postRef.collection("likedUsers").document(uid!!)
            .set(
                mapOf(
                    "id" to userId, // いいねをつけたユーザーのid
                    // "createTime" to FieldValue.serverTimestamp()
                )
            )

        val likedUsersRef = postRef.collection("likedUsers")
        likedUsersRef.addSnapshotListener { snapshot, e ->
            likedCount = snapshot?.size() ?: 0
            Log.d("like", likedCount.toString())

            postRef
                .update("likeCount", likedCount)
                .addOnSuccessListener {

                }
        }
    }

    // いいねを取得
    fun getLikedUsers(postId: String) {
        // いいねしたユーザー最大２０件のドキュメント
        val likedUserSnapshot = db.collection("post").document(postId)
            .collection("likedUsers")
            .orderBy("createTime", Query.Direction.DESCENDING)
            .limit(20)
            .get()

        // todo 非同期処理に対応してユーザーの参照を取得
        // todo ページング処理
    }

    fun getPoint(binding: FragmentProfileListBinding, context: Context) {
        userRef
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val point = snapshot.getLong("point")!!.toString()
                    binding.pointText.text = context.getString(R.string.current_point, point)
                }
            }
    }

    fun insertAdsForPost(uid: String, postId: String) {
        userDocumentRef.document(uid)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val otherName = snapshot.getString("name")
                    userDocumentRef.document(uid).collection("advertise").document(postId)
                        .get()
                        .addOnSuccessListener { advertiseSnapshot ->
                            val postId = advertiseSnapshot.id
                            val body = advertiseSnapshot.getString("body")
                            val myPostRef = userRef.collection("post").document() // ドキュメントIDを自動生成
                            imageRef = postId
                            myPostRef.set(
                                mapOf(
                                    "otherName" to otherName,
                                    "postId" to postId,
                                    "body" to body,
                                    "likeCount" to 0, // いいねされた数
                                    "type" to 2

                                )
                            )
                        }
                }
            }
    }

    fun getData(itemListAdapter: ItemListAdapter, fragment: Fragment) {
        userDocumentRef
            .addSnapshotListener { snapshot, e -> // users
                if (snapshot != null) {
                    val tasks = mutableListOf<Task<QuerySnapshot>>() // 非同期タスクのリストを作成
                    val postList = mutableListOf<Post>()
                    for (userDocument in snapshot.documents) {
                        Log.d("getData", "documentが存在する")
                        val address = userDocument.getString("macAddress")
                            val currentList = tmpList.value?.toList()
                            if (currentList != null && address!! in currentList) { // ここですれ違い
                                val author = userDocument.getString("name")
                                val matchUid = userDocument.id
                                val matchedUserRef = userRef.collection("alreadyMatchedUsers")
                                // 既にマッチしたユーザーのリストを取得する
                                matchedUserRef.get()
                                    .addOnSuccessListener { querySnapshot ->
                                        val matchedUserIds = querySnapshot.documents.map { it.getString("userId") }

                                        if (matchUid !in matchedUserIds) {
                                            // まだマッチしていない場合か、一致するIDがない場合、新しいドキュメントを作成
                                            userDocumentRef.document(matchUid).collection("post")
                                                .get()
                                                .addOnSuccessListener { querySnapshot ->
                                                    if (!querySnapshot.isEmpty) {
                                                        matchedUserRef.add(mapOf("userId" to matchUid))
                                                            .addOnSuccessListener {
                                                                userDocumentRef.document(matchUid)
                                                                    .get()
                                                                    .addOnSuccessListener { snapshot->
                                                                        Log.d("point", "true")
                                                                        var point =snapshot.getLong("point")!!.toInt()
                                                                        point += 10
                                                                        Log.d("point", "現在のポイントは" + point)

                                                                        userDocumentRef.document(matchUid)
                                                                            .update("point", point)
                                                                    }
                                                            }
                                                            .addOnFailureListener { exception ->
                                                            }
                                                    }
                                                }

                                        } else {
                                            Log.d("current", "既にマッチしています")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("current", "クエリの実行に失敗しました：$exception")
                                    }

                                val task = userDocumentRef.document(matchUid).collection("post")
                                    .get()
                                    .addOnSuccessListener { querySnapshot ->
                                        for (documentSnapshot in querySnapshot.documents) {
                                            var otherName = ""
                                            if (documentSnapshot.getString("otherName") != null) {
                                                otherName = documentSnapshot.getString("otherName").toString()
                                            }
                                            val postId = documentSnapshot.id
                                            val body = documentSnapshot.getString("body")
                                            val type = documentSnapshot.getLong("type")!!.toInt()
                                            val currentLikedCount = documentSnapshot.getLong("likeCount")!!.toInt()
                                            Log.d("likedCount", currentLikedCount.toString() + "がいいね数です")
                                            lateinit var imageRef: String
                                            if (type == 1) {
                                                imageRef = postId
                                            } else {
                                                imageRef = documentSnapshot.getString("postId").toString()
                                            }

                                            val userPost = Post(
                                                uid = matchUid,
                                                postId = postId,
                                                body = body!!,
                                                likedCount = currentLikedCount,
                                                image = storageRef.child(imageRef),
                                                author = author!!,
                                                type = type,
                                                otherAuthor = otherName
                                            )

                                            if (!postList.contains(userPost)) {
                                                postList.add(userPost)
                                            }
                                        }
                                    }
                                tasks.add(task)
                                Tasks.whenAllSuccess<DocumentSnapshot>(tasks) // すべての非同期タスクが完了するまで待機
                                    .addOnSuccessListener {
                                        itemListAdapter.updateList(postList)
                                    }
                            }
                        }
                }
            }
    }

    fun getAdvertise(advertiseAdapter: AdvertiseAdapter) {
        // userDocumentRef
        db.collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("snapshot", e.toString() + "のエラーです")
                }

                if (snapshot != null) {
                    Log.d("snapshot", "addSnapshotが動いています")
                    val advertiseList = mutableListOf<Post>()
                    val tasks = mutableListOf<Task<QuerySnapshot>>() // 非同期タスクのリストを作成
                    Log.d("snapshot", "advertiseListが作られました")
                        for (userDocument in snapshot.documents) {
                        val uid = userDocument.id
                        val author = userDocument.getString("name")
                        val advertiseRef = userDocumentRef.document(uid).collection("advertise")

                            val task = advertiseRef
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                for (documentSnapshot in querySnapshot.documents) {
                                    val advertiseId = documentSnapshot.id
                                    val body = documentSnapshot.getString("body").toString()
                                    val currentLikedCount = 0
                                    Log.d("likedCount", currentLikedCount.toString() + "がいいね数です")

                                    val advertise = Post(
                                        uid = uid,
                                        postId = advertiseId,
                                        body = body,
                                        likedCount = currentLikedCount,
                                        image = storageRef.child(advertiseId),
                                        author = author!!,
                                        //createTime = createTime
                                        otherAuthor = "",
                                        type = 0
                                    )
                                    Log.d("snapshot", advertise.toString())

                                    if (!advertiseList.contains(advertise)) {
                                        advertiseList.add(advertise)
                                        Log.d("advertiseList", advertiseList.toString())
                                    }
                                    //advertiseAdapter.submitList(advertiseList)
                                    Log.d("snapshot", "現在のリスト" + advertiseList.toString())
                                }
                            }
                            tasks.add(task)
                            Tasks.whenAllSuccess<DocumentSnapshot>(tasks) // すべての非同期タスクが完了するまで待機
                                .addOnSuccessListener {
                                    // advertiseAdapter.submitList(advertiseList) // UIの更新
                                    advertiseAdapter.updateList(advertiseList)
                                }
                    }
                }
            }

    }
}
