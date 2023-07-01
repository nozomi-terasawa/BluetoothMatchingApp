package com.example.bluettoothmatching.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bluettoothmatching.database._uid
import com.example.bluettoothmatching.database.uid
import com.example.bluettoothmatching.databinding.FragmentInitialScreenBinding
import com.google.firebase.auth.FirebaseAuth

class InitialScreenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentInitialScreenBinding? = null
    private val binding get() = _binding!!
    private var auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInitialScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // activity?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
        
        // login
        binding.loginButton.setOnClickListener {
                val loginUserEmail = binding.loginUserEmailInput.text.toString()
                val loginUserPassword = binding.loginUserPasswordInput.text.toString()
                if (!loginUserEmail.isNullOrEmpty() && !loginUserPassword.isNullOrEmpty()) {
                    auth.signInWithEmailAndPassword(loginUserEmail, loginUserPassword)
                        .addOnCompleteListener() { task ->
                            if (task.isSuccessful) {
                                _uid = auth.currentUser?.uid // 現在ログインしているユーザーのUIDを取得
                                Log.d("testUid", uid.toString())
                                val action =
                                    InitialScreenFragmentDirections.actionInitialScreenFragmentToProfileListFragment()
                                this.findNavController().navigate(action)
                            }
                        }
                } else {
                    Toast.makeText(context, "メールアドレスとパスワードを正しく入力してください", Toast.LENGTH_SHORT).show()
                }
        }

        binding.signupButton.setOnClickListener {
            val action = InitialScreenFragmentDirections.actionInitialScreenFragmentToSignUpFragment()
            this.findNavController().navigate(action)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        //activity?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.VISIBLE

        _binding = null
    }
}