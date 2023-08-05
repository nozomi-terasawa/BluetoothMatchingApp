package com.example.bluettoothmatching.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bluettoothmatching.MainActivity
import com.example.bluettoothmatching.R
import com.example.bluettoothmatching.database.FireStore
import com.example.bluettoothmatching.databinding.FragmentCreateProfileBinding

class CreateProfileFragment : Fragment() {

    private val fireStore = FireStore()
    private lateinit var userAddress: String
    private lateinit var userName: String
    private lateinit var userInfo: String

    private var _binding: FragmentCreateProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // navigationDrawerの非表示
        val mainActivity = requireActivity() as MainActivity
        val drawerLayout = mainActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
        val action = mainActivity.getActionBarDrawerToggle()
        action.isDrawerIndicatorEnabled = false
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

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
                fireStore.createProfile(userAddress, userName, userInfo)
                val action = CreateProfileFragmentDirections.actionCreateProfileFragmentToProfileListFragment()
                this.findNavController().navigate(action)
            } else {
                Toast.makeText(context, "すべての項目を入力してください", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

