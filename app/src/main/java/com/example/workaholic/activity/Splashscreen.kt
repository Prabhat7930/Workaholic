@file:Suppress("DEPRECATION")

package com.example.workaholic.activity

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.TextView
import com.example.workaholic.R
import com.example.workaholic.firebase.FireStoreClass


class Splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val appName : TextView = findViewById(R.id.tv_app_name)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val tFace : Typeface = Typeface.createFromAsset(assets, "splashFont.ttf")
        appName.typeface = tFace

        Handler().postDelayed({

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