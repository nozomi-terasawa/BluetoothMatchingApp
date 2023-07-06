package com.example.bluettoothmatching.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bluettoothmatching.database.FireBaseStorage
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.databinding.FragmentCreateProfileBinding

class CreateProfileFragment : Fragment() {

    private var _binding: FragmentCreateProfileBinding? = null
    private val binding get() = _binding!!
    private val storage = FireBaseStorage()
    private val fireStore = FireStore()
    private lateinit var userAddress: String
    private lateinit var userName: String
    private lateinit var userInfo: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener {
            userName = binding.userNameInput.text.toString()
            userInfo = binding.userInfo.text.toString()
            val _address = binding.userAddressInput.text.toString()
            val newAddress = StringBuilder()
            for (i in 0 until _address.length) {
                newAddress.append(_address[i])
                if ((i + 1) % 2 == 0 && i < _address.length - 1) {
                    newAddress.append(":")
                }
            }
            userAddress = newAddress.toString()

            if (!userName.isNullOrEmpty() && !userInfo.isNullOrEmpty() && !userAddress.isNullOrEmpty()) {
                fireStore.insertData(userAddress, userName, userInfo)
                val action = CreateProfileFragmentDirections.actionCreateProfileFragmentToProfileListFragment()
                this.findNavController().navigate(action)
            } else {
                Toast.makeText(context, "すべての項目を入力してください", Toast.LENGTH_SHORT).show()
            }
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
            ProfileListFragment.READ_REQUEST_CODE -> {
                try {
                    resultData?.data?.also { uri ->
                        imageUri = uri // 画像のURI取得
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
        startActivityForResult(intent, ProfileListFragment.READ_REQUEST_CODE)
    }
}
