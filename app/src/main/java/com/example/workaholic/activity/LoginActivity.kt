@file:Suppress("DEPRECATION")

package com.example.workaholic.activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.example.workaholic.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.btnSignUp1.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnSignIn1.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}