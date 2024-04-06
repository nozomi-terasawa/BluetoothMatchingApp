package com.example.bluettoothmatching.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluettoothmatching.adapter.ItemListAdapter
import com.example.bluettoothmatching.databinding.FragmentPastProfileListBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot


class PastProfileListFragment : Fragment() {

    private var _binding: FragmentPastProfileListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPastProfileListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemListAdapter = ItemListAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = itemListAdapter
        // itemListAdapter.submitList(allList)
        //fireStore.getData(itemListAdapter, this)

        val tasks = mutableListOf<Task<QuerySnapshot>>()
        /**
        val allPostRef = db.collection("allPost")
        val allPostList = mutableListOf<Post>()
        val task = allPostRef
            .get()
            .addOnSuccessListener { document ->
                for (documentSnapshot in document.documents) {
                    val uid = documentSnapshot.getString("uid")
                    val author = documentSnapshot.getString("author")
                    val body = documentSnapshot.getString("body")
                    val _image = documentSnapshot.getString("image")
                    val image = storage.getReferenceFromUrl(_image!!)
                    val likedCount = documentSnapshot.getLong("likedCount")
                    val otherAuthor = documentSnapshot.getString("otherAuthor")
                    val postId = documentSnapshot.getString("postId")
                    val type = documentSnapshot.getLong("type")!!.toInt()
                    val color = documentSnapshot.getString("color")

                    val allPost = Post(
                        uid = uid!!,
                        postId = postId!!,
                        body = body!!,
                        likedCount = likedCount?.toInt()!!,
                        image = image!!,
                        author = author!!,
                        type = type,
                        otherAuthor = otherAuthor!!,
                        color = color!!
                    )

                    allPost.let {
                        if (!allPostList.contains(allPost) && uid != allPost.uid) {
                            allPostList.add(allPost)
                        }
                    }
                }
            }
        tasks.add(task)
        Tasks.whenAllSuccess<DocumentSnapshot>(tasks) // すべての非同期タスクが完了するまで待機
            .addOnSuccessListener {
                itemListAdapter.submitList(allPostList)
            }
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}