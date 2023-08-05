package com.example.bluettoothmatching

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.bluettoothmatching.bluetooth.BlutoothBK
import com.example.bluettoothmatching.databinding.ActivityMainBinding
import com.example.bluettoothmatching.fragment.ProfileListFragment
import com.example.bluettoothmatching.fragment.ProfileListFragmentDirections
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

lateinit var navController: NavController


class MainActivity : AppCompatActivity() {

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private var auth = FirebaseAuth.getInstance()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    fun getActionBarDrawerToggle(): ActionBarDrawerToggle {
        return actionBarDrawerToggle
    }

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
                R.id.show_advertise -> R.id.advertiseListFragment
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

        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationDrawer
        val toolbar = binding.toolbar

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_bar, R.string.close_bar
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()



        val navigationView = binding.navigationDrawer
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.on_bluetooth -> {
                    val intent = Intent(this, BlutoothBK::class.java)
                    this.startForegroundService(intent);

                    // メニュー項目1が選択されたときの処理
                    Log.d("nav", "true")
                    true
                }

                R.id.of_bluetooth -> {
                    val intent = Intent(this, BlutoothBK::class.java)
                    this.stopService(intent);
                    // メニュー項目2が選択されたときの処理
                    Log.d("nav", "true")
                    true
                }

                R.id.edit_profile -> {
                    Log.d("nav", "true")
                    val action =
                        ProfileListFragmentDirections.actionProfileListFragmentToUpDateProfileFragment32()
                    navController.navigate(action)
                    true
                }
                // 他のメニュー項目に対する処理を追加
                R.id.seemy -> {
                    val discoverableIntent: Intent =
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                        }
                    startActivity(discoverableIntent)
                    true
                }
                else -> {
                    true
                }
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