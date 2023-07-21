package com.example.bluettoothmatching.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluettoothmatching.ItemListAdapter
import com.example.bluettoothmatching.data.Profile
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.databinding.FragmentPastProfileListBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

var allList: MutableList<Profile> = mutableListOf() // todo　livedataにする
private val fireStore = FireStore()
private val db = Firebase.firestore

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

        val itemListAdapter = ItemListAdapter({
            val action =
                ProfileListFragmentDirections.actionProfileListFragmentToProfileDetailFragment2() // ToDo navgraphで引数を渡す
            this.findNavController().navigate(action)
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = itemListAdapter
        // itemListAdapter.submitList(allList)
        //fireStore.getData(itemListAdapter, this)

        val tasks = mutableListOf<Task<QuerySnapshot>>()

/*        val allUserRef = db.collection("allusers")
        val task = allUserRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (documentSnapshot in querySnapshot) {
                    val profile = documentSnapshot.toObject(Profile::class.java)

                    profile?.let {
                        if (!allList.contains(profile)) {
                            allList.add(profile)
                        }
                    }
                }
            }
        tasks.add(task)
        Tasks.whenAllSuccess<DocumentSnapshot>(tasks) // すべての非同期タスクが完了するまで待機
            .addOnSuccessListener {
                itemListAdapter.submitList(allList)
            }

 */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}