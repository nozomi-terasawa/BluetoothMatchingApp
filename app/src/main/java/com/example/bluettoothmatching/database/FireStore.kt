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
                    "point" to 0
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
        Log.d("getData", "開始")
        userDocumentRef
            .addSnapshotListener { snapshot, e -> // users
                if (snapshot != null) {
                    Log.d("getData", "snapshotが存在する")
                    val postList = mutableListOf<Post>()
                    val tasks = mutableListOf<Task<QuerySnapshot>>() // 非同期タスクのリストを作成
                    //tmpList.observe(fragment.viewLifecycleOwner, {
                    for (userDocument in snapshot.documents) {
                        Log.d("getData", "documentが存在する")
                        val address = userDocument.getString("macAddress")
                        // tmpList.observe(fragment.viewLifecycleOwner, {
                            Log.d("getData", "オブザーバー突入")
                            val currentList = tmpList.value?.toList()
                            if (currentList != null && address!! in currentList) {
                                val author = userDocument.getString("name")
                                val matchUid = userDocument.id
                                val task = userDocumentRef.document(matchUid).collection("post")
                                    .get()
                                    .addOnSuccessListener { querySnapshot ->
                                        for (documentSnapshot in querySnapshot.documents) {
                                            // todo type2のデータに、otherPostIdを作り、それをimageRefとして渡す。if文で分ける
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
                                            //val createTime = FieldValue.serverTimestamp()

                                            val userPost = Post(
                                                uid = matchUid,
                                                postId = postId,
                                                body = body!!,
                                                likedCount = currentLikedCount,
                                                image = storageRef.child(imageRef),
                                                author = author!!,
                                                type = type,
                                                otherAuthor = otherName
                                                //createTime = createTime
                                            )

                                            if (!postList.contains(userPost)) {
                                                postList.add(userPost)
                                                Log.d("getData", postList.toString())
                                            }
                                            //itemListAdapter.submitList(postList)
                                            Log.d("getData", "画面の更新")
                                        }
                                        /*
                                        itemListAdapter.submitList(postList)
                                        Log.d("getData", "画面の更新")

                                         */
                                    }
                                tasks.add(task)
                                Tasks.whenAllSuccess<DocumentSnapshot>(tasks) // すべての非同期タスクが完了するまで待機
                                    .addOnSuccessListener {
                                        itemListAdapter.updateList(postList)
                                        // itemListAdapter.submitList(postList)  { } UIの更新
                                    }
                            }
                        }
                    // })
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
