package com.example.bluettoothmatching.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluettoothmatching.ItemListAdapter
import com.example.bluettoothmatching.bluetooth.BlutoothBK
import com.example.bluettoothmatching.bluetooth.tmpList
import com.example.bluettoothmatching.databinding.FragmentProfileListBinding
import com.example.firestoresample_todo.database.Profile
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfileListFragment : Fragment() {

    private var _binding: FragmentProfileListBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemListAdapter = ItemListAdapter({
            val action = ProfileListFragmentDirections.actionProfileListFragmentToProfileDetailFragment2() // ToDo navgraphで引数を渡す
            this.findNavController().navigate(action)
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = itemListAdapter

        // fireStore.getData(itemListAdapter, this)
        Log.d("FUJI", "true")

        val profileList = ArrayList<Profile>() // [Profile(address="", name="", message=""),...]
        val tasks = mutableListOf<Task<QuerySnapshot>>() // 非同期タスクのリストを作成

        db.collection("users") // CollectionReference
            .addSnapshotListener { profile, e -> // profileは取得されたドキュメントのsnapshot addSnapshotでリアルタイム更新
                tmpList.observe(viewLifecycleOwner, {
                    val size = tmpList.value?.size ?: 0
                    for (i in 0 until size) {
                        val item = tmpList.value?.get(i)
                        val collectionRef =
                            db.collection("users") // todo usersのドキュメントフィールドのaddressがscannedAddress[i]のドキュメントにアクセス
                        val query = collectionRef.whereEqualTo(
                            "address",
                            item
                        ).orderBy("address", Query.Direction.ASCENDING) // addressがtmpListの中にあれば、そのコレクションを参照
                        val task = query.get() // todo ドキュメントフィールドのaddressを参照しなければいけない
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) { // クエリ結果が空ではない場合にのみログを出力
                                    Log.d("FUJI", "成功" + item)
                                    for (documentSnapshot in querySnapshot.documents) {
                                        val profile =
                                            documentSnapshot.toObject(Profile::class.java)
                                        Log.d("FUJI",profile.toString())
                                        profile?.let {
                                            // if (!profileList.contains(profile)) {
                                                profileList.add(profile)
                                            //}
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


        binding.pastButton.setOnClickListener {
            val action = ProfileListFragmentDirections.actionProfileListFragmentToPastProfileListFragment2()
            this.findNavController().navigate(action)
        }

        binding.button.setOnClickListener { //スタート
            val intent = Intent(requireActivity(), BlutoothBK::class.java)
            requireActivity().startForegroundService(intent);


        }

        binding.button2.setOnClickListener {
            val intent = Intent(requireContext() , BlutoothBK::class.java)
            requireActivity(). stopService(intent);
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
