package com.example.bluettoothmatching.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bluettoothmatching.database.MyFirebaseAuth
import com.example.bluettoothmatching.databinding.FragmentSigupBinding

class SignUpFragment : Fragment() {
    private var _binding: FragmentSigupBinding? = null
    private val binding get() = _binding!!
    private val auth = MyFirebaseAuth()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSigupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // todo ６文字以上のパスワードとメールアドレスの書式を強制刺せる
        binding.signupButton.setOnClickListener {
            val userPassword = binding.userPasswordInput.text.toString()
            val userEmail = binding.emailInput.text.toString()
            if (!userPassword.isNullOrEmpty() && !userEmail.isNullOrEmpty()) {
                auth.signUp(userEmail, userPassword) // 登録
                val action = SignUpFragmentDirections.actionSignUpFragmentToCreateProfileFragment()
                this.findNavController().navigate(action)
            } else {
                Toast.makeText(context, "メールアドレスとパスワードを正しく入力してください", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}