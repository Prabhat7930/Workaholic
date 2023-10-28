package com.example.workaholic.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.workaholic.R
import com.example.workaholic.databinding.ActivitySignInBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    private lateinit var binding : ActivitySignInBinding

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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

        setupActionBar()

        binding.btnSignIn2.setOnClickListener {
            loginUser()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignIn)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }

        binding.toolbarSignIn.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun loginUser() {
        val email : String = binding.etEmail2.text.toString().trim {it <= ' '}
        val password : String = binding.etPassword2.text.toString().trim {it <= ' '}

        if (validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        FireStoreClass().loadUserData(this@SignInActivity)
                    }
                    else {
                        hideProgressDialog()
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(email : String, password : String) : Boolean{
        return when {
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter email id")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter the password")
                false
            }
            else->{
                true
            }
        }
    }

    fun loginSuccess(user : User) {
        hideProgressDialog()
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }
}