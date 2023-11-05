package com.example.workaholic.activity


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
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
            val intent = Intent(this@LoginActivity, SignInActivity::class.java)
            startMainActivityWhenLoginSuccess.launch(intent)
        }
    }

    private val startMainActivityWhenLoginSuccess =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            if (result.resultCode == 1) {
                finish()
            }
            else {
                Log.e("BackPressed", "Invoked")
            }
        }
}