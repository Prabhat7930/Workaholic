package com.example.workaholic.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.example.workaholic.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }


        binding.btnSignUp1.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

        binding.btnSignIn1.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignInActivity::class.java))
            finish()
        }
    }
}