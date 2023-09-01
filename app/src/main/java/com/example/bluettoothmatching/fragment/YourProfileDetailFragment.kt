package com.example.bluettoothmatching.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluettoothmatching.adapter.ItemListAdapter
import com.example.bluettoothmatching.data.Post
import com.example.bluettoothmatching.database.imageRef
import com.example.bluettoothmatching.databinding.FragmentYourProfileDetailBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class YourProfileDetailFragment : Fragment() {
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private var _binding: FragmentYourProfileDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYourProfileDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: YourProfileDetailFragmentArgs by navArgs()
        val uid = args.uid
        val postList = mutableListOf<Post>()
        val itemListAdapter = ItemListAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = itemListAdapter

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val name = snapshot.getString("name")
                val introduction = snapshot.getString("introduction")

                binding.nameText.text = name
                binding.introductionText.text = introduction
                db.collection("users").document(uid)
                    .collection("post")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (snapshot in querySnapshot.documents) {
                            val uid = snapshot.id
                            var otherName = ""
                            if (snapshot.getString("otherName") != null) {
                                otherName =
                                    snapshot.getString("otherName").toString()
                            }
                            val postId = snapshot.id
                            val body = snapshot.getString("body")
                            val color = snapshot.getString("color")
                            val type = snapshot.getLong("type")!!.toInt()
                            val currentLikedCount = snapshot.getLong("likeCount")!!.toInt()
                            if (type == 1) {
                                imageRef = postId
                            } else {
                                imageRef =
                                    snapshot.getString("postId").toString()
                            }

                            val userPost = Post(
                                uid = uid,
                                postId = postId,
                                body = body!!,
                                likedCount = currentLikedCount,
                                image = storageRef.child(imageRef!!),
                                author = name!!,
                                type = type,
                                otherAuthor = otherName,
                                color = color!!
                            )
                            if (!postList.contains(userPost)) {
                                postList.add(userPost)
                                itemListAdapter.submitList(postList)
                            }
                        }
                    }


                db.collection("users").document(uid)
                    .collection("advertise")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (snapshot in querySnapshot.documents) {
                            val uid = snapshot.id
                            val advertiseId = snapshot.id
                            val body = snapshot.getString("body")
                            val color = snapshot.getString("color")
                            // val type = snapshot.getLong("type")!!.toInt()
                            // val currentLikedCount = snapshot.getLong("likeCount")!!.toInt()

                            val userPost = Post(
                                uid = uid,
                                postId = advertiseId,
                                body = body!!,
                                likedCount = 0,
                                image = storageRef.child(advertiseId),
                                author = name!!,
                                type = 0,
                                otherAuthor = "",
                                color = color!!
                            )
                            if (!postList.contains(userPost)) {
                                postList.add(userPost)
                                itemListAdapter.submitList(postList)
                            }
                        }
                    }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}