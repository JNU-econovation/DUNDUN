package com.project981.dundun

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project981.dundun.databinding.ActivityMainBinding
import com.project981.dundun.view.MainViewModel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding : ActivityMainBinding
        get() = requireNotNull(_binding)

    private val viewModel : MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네비게이션 컨트롤러
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)
        // bottomNav 객체 등록
        bottomNav.setupWithNavController(navController)


        bottomNav.setOnItemReselectedListener {
            viewModel.key = it.itemId
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}