package com.example.bluettoothmatching.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.bluettoothmatching.MainActivity
import com.example.bluettoothmatching.R
import com.example.bluettoothmatching.database.MyFirebaseAuth
import com.example.bluettoothmatching.databinding.FragmentInitialScreenBinding
import com.google.firebase.auth.FirebaseAuth

class InitialScreenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentInitialScreenBinding? = null
    private val binding get() = _binding!!
    private var auth = FirebaseAuth.getInstance()
    private val myAuth = MyFirebaseAuth()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInitialScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE

        // navigationDrawerの非表示
        val mainActivity = requireActivity() as MainActivity
        val drawerLayout = mainActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
        val action = mainActivity.getActionBarDrawerToggle()
        action.isDrawerIndicatorEnabled = false
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //トップメニューの非表示
        val toolbar = mainActivity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.visibility = View.GONE

        // login
        binding.loginButton.setOnClickListener {
            val loginUserEmail = binding.loginUserEmailInput.text.toString()
            val loginUserPassword = binding.loginUserPasswordInput.text.toString()
            if (!loginUserEmail.isNullOrEmpty() && !loginUserPassword.isNullOrEmpty()) {
                myAuth.login(loginUserEmail, loginUserPassword)
            } else {
            Toast.makeText(context, "メールアドレスとパスワードを正しく入力してください", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupButton.setOnClickListener {
            val userPassword = binding.loginUserPasswordInput.text.toString()
            val userEmail = binding.loginUserEmailInput.text.toString()
            if (!userPassword.isNullOrEmpty() && !userEmail.isNullOrEmpty()) {
                myAuth.signUp(userEmail, userPassword)
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