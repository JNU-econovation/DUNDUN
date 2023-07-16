package com.project981.dundun

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.project981.dundun.R
import com.project981.dundun.view.signin.SigninViewModel

class SignActivity : AppCompatActivity() {
    private val viewModel: SigninViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        viewModel.isLogin.observe(this){
            if(it){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}