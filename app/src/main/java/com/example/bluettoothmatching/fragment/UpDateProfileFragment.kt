package com.example.bluettoothmatching.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bluettoothmatching.database.MyFirebaseAuth
import com.example.bluettoothmatching.databinding.FragmentUpDateProfileBinding

private val myFirebaseAuth = MyFirebaseAuth()

class UpDateProfileFragment : Fragment() {
    private var _binding: FragmentUpDateProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpDateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveButton.setOnClickListener {
            val userName = binding.userNameInput.text.toString()
            val userInfo = binding.userInfo.text.toString()
            val _address = binding.userAddressInput.text.toString()
            val newAddress = StringBuilder()
            for (i in 0 until _address.length) {
                newAddress.append(_address[i])
                if ((i + 1) % 2 == 0 && i < _address.length - 1) {
                    newAddress.append(":")
                }
            }
            val userAddress = newAddress.toString()

            if (!userName.isNullOrEmpty() && !userInfo.isNullOrEmpty() && !userAddress.isNullOrEmpty()) {
                myFirebaseAuth.upDate(userAddress, userName, userInfo)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}