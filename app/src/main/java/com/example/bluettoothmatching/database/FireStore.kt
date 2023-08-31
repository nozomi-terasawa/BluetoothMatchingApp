package com.example.bluettoothmatching.database

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bluettoothmatching.R
import com.example.bluettoothmatching.adapter.AdvertiseAdapter
import com.example.bluettoothmatching.adapter.ItemListAdapter
import com.example.bluettoothmatching.bluetooth.BlutoothBK.Companion.tuuti_ID
import com.example.bluettoothmatching.bluetooth.tmpList
import com.example.bluettoothmatching.data.Post
import com.example.bluettoothmatching.databinding.FragmentCreatePostBinding
import com.example.bluettoothmatching.databinding.FragmentProfileListBinding
import com.example.bluettoothmatching.databinding.RepostAdsItemBinding
import com.example.bluettoothmatching.databinding.UserProfileItemBinding
import com.example.bluettoothmatching.fragment.CreatePostFragmentDirections
import com.example.bluettoothmatching.navController
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
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
    private lateinit var fragmentContext: Context

    private val userDocumentRef = db.collection("users")
    private val userRef = userDocumentRef.document(uid!!)

    // livedata
    private val _postListData: MutableLiveData<List<Post>> = MutableLiveData()
    val postListLiveData: LiveData<List<Post>> = _postListData

    fun createProfile(macAddress: String, name: String, introduction: String) {
        userRef
            .set(
                mapOf(
                    "macAddress" to macAddress,
                    "name" to name,
                    "introduction" to introduction,
                    "point" to 0,
                )
            )
    }

    // 投稿
    fun post(body: String, color: String) {
        val postRef = userRef.collection("post").document() // ドキュメントIDを自動生成
        imageRef = postRef.id
        postRef.set(
            mapOf(
                "body" to body,
                "likeCount" to 0,
                "type" to 1,
                "createTime" to FieldValue.serverTimestamp(),
                "postId" to postRef.id,
                "color" to color
            )
        )
            .addOnSuccessListener {
            }
    }

    fun advertise(body: String, color: String) {
        val advertiseRef = userRef.collection("advertise").document()
        imageRef = advertiseRef.id
        advertiseRef.set(
            mapOf(
                "body" to body,
                "postId" to advertiseRef.id,
                "createTime" to FieldValue.serverTimestamp(),
                "color" to color
            )
        )
            .addOnSuccessListener {
            }
    }

    fun addLikedUserToPost(userId: String, postId: String, binding: UserProfileItemBinding) {
        val userRef = userDocumentRef.document(userId)
        val postRef = userRef.collection("post").document(postId)
        // サブコレクションを追加
        postRef.collection("likedUsers").document(uid!!)
            .set(
                mapOf(
                    "id" to userId, // いいねをつけたユーザーのid
                )
            )

        val likedUsersRef = postRef.collection("likedUsers")
        likedUsersRef.addSnapshotListener { snapshot, e ->
            likedCount = snapshot?.size() ?: 0
            postRef
                .update("likeCount", likedCount)
                .addOnSuccessListener {
                    binding.likeCount.text = likedCount.toString()
                }
        }
    }

    fun addLikedUserToPost2(userId: String, postId: String, binding: RepostAdsItemBinding) {
        val userRef = userDocumentRef.document(userId)
        val postRef = userRef.collection("post").document(postId)
        // サブコレクションを追加
        postRef.collection("likedUsers").document(uid!!)
            .set(
                mapOf(
                    "id" to userId, // いいねをつけたユーザーのid
                )
            )

        val likedUsersRef = postRef.collection("likedUsers")
        likedUsersRef.addSnapshotListener { snapshot, e ->
            likedCount = snapshot?.size() ?: 0
            postRef
                .update("likeCount", likedCount)
                .addOnSuccessListener {
                    binding.likeCount.text = likedCount.toString()
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

    fun currentPoint(binding: FragmentCreatePostBinding, context: Context) {
        userRef
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val point = snapshot.getLong("point")!!.toString()
                    binding.currentPoint.text = context.getString(R.string.current_point, point)
                }
            }
    }

    fun usePoint() {
        var flag = true
        Log.d("usePoint", "ture")
        userRef
            .addSnapshotListener { snapshot, e ->
                if (flag == true) {
                    if (snapshot != null) {
                        var point = snapshot.getLong("point")
                        if (point!! >= 10) {
                            point -= 10
                            userRef
                                .update("point", point)
                            flag = false
                        }
                    }
                }
            }
    }

    fun usePoint2() {
        var flag = true
        Log.d("usePoint", "ture")
        userRef
            .addSnapshotListener { snapshot, e ->
                if (flag == true) {
                    if (snapshot != null) {
                        var point = snapshot.getLong("point")
                        if (point!! >= 20) {
                            point -= 20
                            userRef
                                .update("point", point)
                            flag = false
                        }
                    }
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
                            val color = advertiseSnapshot.getString("color")
                            imageRef = postId
                            userRef.collection("post") // todo ドキュメントIDを自動生成
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    val allPosts = querySnapshot.documents.map { it.getString("postId") }
                                    if (postId !in allPosts) {
                                        //todo getDataと同じ要領で重複をなくす
                                        userRef.collection("post").document().set(
                                            mapOf(
                                                "otherName" to otherName,
                                                "postId" to postId,
                                                "body" to body,
                                                "likeCount" to 0, // いいねされた数
                                                "type" to 2,
                                                "createTime" to FieldValue.serverTimestamp(),
                                                "color" to color

                                            )
                                        )
                                    }
                                }
                        }
                }
            }
    }

    fun getData(itemListAdapter: ItemListAdapter, context: Context) {
        fragmentContext = context
        Log.d("getData", "true")
        userDocumentRef
            .addSnapshotListener { snapshot, e -> // users
                if (snapshot != null) {
                    val tasks = mutableListOf<Task<QuerySnapshot>>() // 非同期タスクのリストを作成
                    val postList = mutableListOf<Post>()
                    for (userDocument in snapshot.documents) {
                        val address = userDocument.getString("macAddress")
                        val currentList = tmpList.value?.toList()
                        if (currentList != null && address!! in currentList) { // ここですれ違い
                            val author = userDocument.getString("name")
                            val matchUid = userDocument.id
                            val matchedUserRef = userRef.collection("alreadyMatchedUsers")
                            // 既にマッチしたユーザーのリストを取得する
                            matchedUserRef.get()
                                .addOnSuccessListener { querySnapshot ->
                                    val matchedUserIds =
                                        querySnapshot.documents.map { it.getString("userId") }
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
                                                                .addOnSuccessListener { snapshot ->
                                                                    val builder = NotificationCompat.Builder(fragmentContext, tuuti_ID)
                                                                    builder.setSmallIcon(androidx.appcompat.R.drawable.abc_ic_menu_copy_mtrl_am_alpha)
                                                                    builder.setContentTitle("マッチング！！！！！")
                                                                    builder.setContentText(author + "さんとマッチング! 10ポイントGet!")
                                                                    val notification = builder.build()
                                                                    val manager = NotificationManagerCompat.from(fragmentContext)
                                                                    manager.notify(100,notification)

                                                                    var point =
                                                                        snapshot.getLong("point")!!
                                                                            .toInt()
                                                                    point += 10
                                                                    userDocumentRef.document(
                                                                        matchUid
                                                                    )
                                                                        .update("point", point)
                                                                }
                                                        }
                                                        .addOnFailureListener { exception ->
                                                        }
                                                }
                                            }
                                    }
                                }
                            val task = userDocumentRef.document(matchUid).collection("post")
                                .orderBy("createTime", Query.Direction.DESCENDING)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    for (documentSnapshot in querySnapshot.documents) {
                                        var otherName = ""
                                        if (documentSnapshot.getString("otherName") != null) {
                                            otherName =
                                                documentSnapshot.getString("otherName").toString()
                                        }
                                        val postId = documentSnapshot.id
                                        val body = documentSnapshot.getString("body")
                                        val type = documentSnapshot.getLong("type")!!.toInt()
                                        val currentLikedCount =
                                            documentSnapshot.getLong("likeCount")!!.toInt()
                                        lateinit var imageRef: String
                                        val color = documentSnapshot.getString("color").toString()
                                        if (type == 1) {
                                            imageRef = postId
                                        } else {
                                            imageRef =
                                                documentSnapshot.getString("postId").toString()
                                        }
                                        val userPost = Post(
                                            uid = matchUid,
                                            postId = postId,
                                            body = body!!,
                                            likedCount = currentLikedCount,
                                            image = storageRef.child(imageRef),
                                            author = author!!,
                                            type = type,
                                            otherAuthor = otherName,
                                            color = color
                                        )
                                        if (!postList.contains(userPost)) {
                                            postList.add(userPost)
                                        }
                                        val postMap = hashMapOf(
                                            "uid" to userPost.uid,
                                            "postId" to userPost.postId,
                                            "body" to userPost.body,
                                            "likedCount" to userPost.likedCount,
                                            "image" to userPost.image.toString(), // 画像の URL など適切な形式に変換
                                            "author" to userPost.author,
                                            "type" to userPost.type,
                                            "otherAuthor" to userPost.otherAuthor,
                                            "color" to userPost.color
                                        )
                                        val allPostRef = db.collection("allPost")
                                        val query = allPostRef.whereEqualTo("postId", userPost.postId)
                                        Log.d("seikou", "成功")
                                        // todo 解読
                                        query.get().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val documents = task.result?.documents
                                                if (documents != null && documents.isNotEmpty()) {
                                                    // 重複が存在する場合の処理
                                                    Log.d("seikou", "重複")
                                                } else {
                                                    // 重複がない場合の処理
                                                    allPostRef.add(postMap)
                                                        .addOnSuccessListener {
                                                            Log.d("seikou", "重複してない")
                                                        }
                                                        .addOnFailureListener {
                                                            // 失敗時の処理
                                                        }
                                                }
                                            }
                                        }
                                    }
                                }
                            tasks.add(task)
                            Tasks.whenAllSuccess<DocumentSnapshot>(tasks) // すべての非同期タスクが完了するまで待機
                                .addOnSuccessListener {
                                    itemListAdapter.submitList(postList)
                                    //_postListData.postValue(postList)
                                }
                        }
                    }
                }
            }
    }

    fun getAdvertise(advertiseAdapter: AdvertiseAdapter) {
        db.collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("snapshot", e.toString() + "のエラーです")
                }

                if (snapshot != null) {
                    val advertiseList = mutableListOf<Post>()
                    val tasks = mutableListOf<Task<QuerySnapshot>>() // 非同期タスクのリストを作成
                    for (userDocument in snapshot.documents) {
                        val uid = userDocument.id
                        val author = userDocument.getString("name")
                        val advertiseRef = userDocumentRef.document(uid).collection("advertise")
                        val task = advertiseRef
                            .orderBy("createTime", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                for (documentSnapshot in querySnapshot.documents) {
                                    val advertiseId = documentSnapshot.id
                                    val body = documentSnapshot.getString("body").toString()
                                    val currentLikedCount = 0
                                    val color = documentSnapshot.getString("color").toString()

                                    val advertise = Post(
                                        uid = uid,
                                        postId = advertiseId,
                                        body = body,
                                        likedCount = currentLikedCount,
                                        image = storageRef.child(advertiseId),
                                        author = author!!,
                                        //createTime = createTime
                                        otherAuthor = "",
                                        type = 0,
                                        color = color
                                    )
                                    if (!advertiseList.contains(advertise)) {
                                        advertiseList.add(advertise)
                                    }
                                }
                            }
                        tasks.add(task)
                        Tasks.whenAllSuccess<DocumentSnapshot>(tasks) // すべての非同期タスクが完了するまで待機
                            .addOnSuccessListener {
                                advertiseAdapter.submitList(advertiseList)
                            }
                    }
                }
            }
    }
}