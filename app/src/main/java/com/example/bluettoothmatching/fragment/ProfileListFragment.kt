package com.example.bluettoothmatching.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluettoothmatching.ItemListAdapter
import com.example.bluettoothmatching.R
import com.example.bluettoothmatching.bluetooth.BlutoothBK
import com.example.bluettoothmatching.database.FireBaseStorage
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.databinding.FragmentProfileListBinding

lateinit var imageUri: Uri
private val storage = FireBaseStorage()
class ProfileListFragment : Fragment() {

    private val fireBaseStorage = FireBaseStorage()

    companion object {
        private const val READ_REQUEST_CODE: Int = 42
    }

    private var _binding: FragmentProfileListBinding? = null
    private val binding get() = _binding!!
    private val fireStore = FireStore()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.VISIBLE

        val itemListAdapter = ItemListAdapter({
            val action = ProfileListFragmentDirections.actionProfileListFragmentToProfileDetailFragment2() // ToDo navgraphで引数を渡す
            this.findNavController().navigate(action)
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = itemListAdapter

        fireStore.getData(itemListAdapter, this)

        binding.button.setOnClickListener { //スタート
            val intent = Intent(requireActivity(), BlutoothBK::class.java)
            requireActivity().startForegroundService(intent);
            //fireBaseStorage.getImage()

        }

        binding.button2.setOnClickListener {
            val intent = Intent(requireContext() , BlutoothBK::class.java)
            requireActivity(). stopService(intent);
        }

        binding.updateButton.setOnClickListener {
            val action = ProfileListFragmentDirections.actionProfileListFragmentToUpDateProfileFragment32()
            this.findNavController().navigate(action)
        }

        binding.imageButton.setOnClickListener {
            selectPhoto()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // intentの結果を受け取る
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode != AppCompatActivity.RESULT_OK) {
            return
        }
        when (requestCode) {
            READ_REQUEST_CODE -> {
                try {
                    resultData?.data?.also { uri ->
                        imageUri = uri // 画像のURI取得
                        // binding.currentProfileList.text = imageUri.toString()
                        storage.uploadImageToFirebaseStorage(imageUri)
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    fun selectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }
}
