package com.example.workaholic.activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.example.workaholic.databinding.ActivitySplashScreenBinding
import com.example.workaholic.firebase.FireStoreClass


class Splashscreen : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
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


        val tFace : Typeface = Typeface.createFromAsset(assets, "splashFont.ttf")
        binding.tvAppName.typeface = tFace

        Handler(Looper.getMainLooper()).postDelayed({
            var currUserId = FireStoreClass().getCurrUserId()

            if (currUserId.isNotEmpty()) {
                startActivity(Intent(this@Splashscreen, MainActivity::class.java))
            }
            else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()

        }, 1000)
    }
}