package com.example.bluettoothmatching

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.bluettoothmatching.databinding.ActivityMainBinding
import com.example.bluettoothmatching.fragment.ProfileListFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            val currentDestination = navController.currentDestination?.id
            val selectedDestination = when (menuItem.itemId) {
                R.id.home -> R.id.profileListFragment
                R.id.odl_list -> R.id.pastProfileListFragment
                else -> null
            }

            if (currentDestination != selectedDestination) {
                selectedDestination?.let { destination ->
                    navController.navigate(destination)
                }
                true
            } else {
                false
            }
        }

        // checkSignInStatus()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }



    // ProfileListFragmentでバックするとアプリを終了
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (currentFragment is NavHostFragment) {
            val fragment = currentFragment.childFragmentManager.primaryNavigationFragment
            if (fragment is ProfileListFragment) {
                finish()
                return
            }
        }
        super.onBackPressed()
    }

    // todo 絶対に消せーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーー
    private fun checkSignInStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // ログイン済みの場合は自動ログイン処理
            navController.navigate(R.id.profileListFragment)
        } else {
            // 未ログインの場合はログイン画面に遷移
            navController.navigate(R.id.initialScreenFragment)
        }
    }



}