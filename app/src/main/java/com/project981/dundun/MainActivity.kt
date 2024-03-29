package com.project981.dundun

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
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
import com.project981.dundun.view.setting.SettingFragment
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
        viewModel.setIsArtist()

        viewModel._progress.observe(this){
            if(it){
                binding.progress.visibility = View.VISIBLE
            }else{
                binding.progress.visibility = View.GONE
            }
        }
        // 네비게이션 컨트롤러
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)
        // bottomNav 객체 등록
        bottomNav.setupWithNavController(navController)

        binding.appbarSetting.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_settingFragment)
        }
        navController.addOnDestinationChangedListener{ _, dest, _ ->
            when(dest.id){
                R.id.writeNoticeFragment, R.id.myPageFragment,R.id.settingFragment -> {
                    bottomNav.visibility = View.GONE
                }
                else -> {

                    bottomNav.visibility = View.VISIBLE
                }
            }

            when(dest.id){
                R.id.homeFragment -> {
                    binding.appbar.visibility = View.VISIBLE
                    binding.appbarSetting.visibility = View.VISIBLE
                }

                R.id.searchFragment, R.id.calendarFragment, R.id.mapFragment -> {

                    binding.appbar.visibility = View.VISIBLE
                    binding.appbarSetting.visibility = View.GONE
                }

                else -> {

                    binding.appbar.visibility = View.GONE

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}