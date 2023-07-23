package com.example.bluettoothmatching.database

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.bluettoothmatching.adapter.AdvertiseAdapter
import com.example.bluettoothmatching.adapter.ItemListAdapter
import com.example.bluettoothmatching.bluetooth.tmpList
import com.example.bluettoothmatching.data.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

var imageRef: String? = null

class FireStore {
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val postList = mutableListOf<Post>()
    private val advertiseList = mutableListOf<Post>()
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
                    "createTime" to FieldValue.serverTimestamp(),
                    "likePostCount" to 0
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
                "author" to userRef,
                "createTime" to FieldValue.serverTimestamp(),
                "likeCount" to 0 // いいねされた数
            )
        )
    }

    fun advertise(body: String) {
        val advertiseRef = userRef.collection("advertise").document()
        imageRef = advertiseRef.id
        advertiseRef.set(
            mapOf(
                "body" to body,
                "author" to userRef,
                "createTime" to FieldValue.serverTimestamp(),
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
                    "createTime" to FieldValue.serverTimestamp()
                )
            )

        val likedUsersRef = userRef.collection("post").document(postId).collection("likedUsers")
        likedUsersRef.addSnapshotListener { snapshot, e ->
            likedCount = snapshot?.size() ?: 0
            Log.d("like", likedCount.toString())
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

    fun getData(itemListAdapter: ItemListAdapter, fragment: Fragment) {
        Log.d("get", "開始")
        userDocumentRef
            .addSnapshotListener { snapshot, e -> // users
                for (userDocument in snapshot!!.documents) {
                    val address = userDocument.getString("macAddress")
                    tmpList.observe(fragment.viewLifecycleOwner, {
                        Log.d("get", "オブザーバー突入")
                        val currentList = tmpList.value?.toList()
                        if (currentList != null && address!! in currentList) {
                            val author = userDocument.getString("name")
                            val matchUid = userDocument.id
                            val postRef = userDocumentRef.document(matchUid).collection("post")
                            postRef
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    for (documentSnapshot in querySnapshot.documents) {
                                        val postId = documentSnapshot.id
                                        val body = documentSnapshot.getString("body")
                                        //val createTime = FieldValue.serverTimestamp()

                                        val userPost = Post(
                                            uid = matchUid,
                                            postId = postId,
                                            body = body!!,
                                            likedCount = likedCount,
                                            image = storageRef.child(postId),
                                            author = author!!,
                                            //createTime = createTime
                                        )

                                        if (!postList.contains(userPost)) {
                                            postList.add(userPost)
                                            Log.d("get", postList.toString())
                                        }
                                    }
                                    itemListAdapter.submitList(postList)
                                    Log.d("get", "画面の更新")
                                }
                        }
                    })
                }
            }
    }

    fun getAdvertise(advertiseAdapter: AdvertiseAdapter) {
        userDocumentRef
            .addSnapshotListener { snapshot, e ->
                for (userDocument in snapshot!!.documents) {
                    val uid = userDocument.id
                    val author = userDocument.getString("name")
                    val advertiseRef = userDocumentRef.document(uid).collection("advertise")
                    advertiseRef
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (documentSnapshot in querySnapshot.documents) {
                                val advertiseId = documentSnapshot.id
                                val body = documentSnapshot.getString("body")

                                val advertise = Post(
                                    uid = uid,
                                    postId = advertiseId,
                                    body = body!!,
                                    likedCount = likedCount,
                                    image = storageRef.child(advertiseId),
                                    author = author!!,
                                    //createTime = createTime
                                )
                                if (!advertiseList.contains(advertise)) {
                                    advertiseList.add(advertise)
                                }
                            }
                            advertiseAdapter.submitList(advertiseList)
                            Log.d("getAdvertise", advertiseList.toString())
                        }
                }
            }

    }





    /*
    fun getData(itemListAdapter: ItemListAdapter, fragment: Fragment) {
            Log.d("FUJI", "true")

            val profileList = ArrayList<Profile>() // [Profile(address="", name="", message=""),...]
            val tasks = mutableListOf<Task<QuerySnapshot>>() // 非同期タスクのリストを作成

            db.collection("users") // CollectionReference
                .addSnapshotListener { profile, e -> // profileは取得されたドキュメントのsnapshot addSnapshotでリアルタイム更新
                    tmpList.observe(fragment.viewLifecycleOwner, { // todo fragmentのインスタンスの取得が遅れるとnullになって、ライフサイクルエラーになる
                        val size = tmpList.value?.size ?: 0
                        for (i in 0 until size) {
                            val item = tmpList.value?.get(i)
                            val collectionRef =
                                db.collection("users")
                            val query = collectionRef.whereEqualTo(
                                "address",
                                item
                            ).orderBy(
                                "address",
                                Query.Direction.ASCENDING
                            ) // addressがtmpListの中にあれば、そのコレクションを参照
                            val task = query.get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) { // クエリ結果が空ではない場合にのみログを出力
                                        Log.d("FUJI", "成功" + item)
                                        for (documentSnapshot in querySnapshot.documents) {
                                            val profile = documentSnapshot.toObject(Profile::class.java)
                                            profile?.let {
                                                if (!profileList.contains(profile)) {
                                                    profileList.add(profile)
                                                    //if (!allList.contains(profile)) {
                                                      //  allList.add(profile)
                                                   // } // todo フラグメントを遷移すると要素が重複する。しかもリストが消える
                                                    //todo ここからinsertAllDataの処理
                                                    val allUserRef = db.collection("allusers")
                                                    allUserRef.add(profile)
                                                        .addOnSuccessListener { documentReference ->
                                                        /*
                                                            documentReference.get()
                                                                .addOnSuccessListener { documentSnapshot ->
                                                                    val addedProfile = documentSnapshot.toObject(Profile::class.java)
                                                                    addedProfile?.let {
                                                                        allList.add(addedProfile)
                                                                        itemListAdapter.submitList(
                                                                            allList) // todo このクエリをpastFragmentでやる
                                                                    }


                                                                }
                                                            */
                                                        }
                                                        .addOnFailureListener {
                                                            Log.d("FUJI", "追加できていません")
                                                        }

                                                }
                                            }
                                        }
                                    } else {
                                        // Log.d("FUJI", "失敗" + item)
                                    }
                                    // Log.d("FUJI", profileList.toString())
                                }
                                .addOnFailureListener { }
                            tasks.add(task) // タスクをリストに追加
                        }
                        Tasks.whenAllSuccess<DocumentSnapshot>(tasks) // すべての非同期タスクが完了するまで待機
                            .addOnSuccessListener {
                                itemListAdapter.submitList(profileList) // UIの更新
                            }
                            .addOnFailureListener {} // 参照の取得に失敗したとき
                    })
                }
        }

     */


}
