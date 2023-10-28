package com.example.workaholic.activity

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.workaholic.R
import com.example.workaholic.databinding.ActivitySignUpBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {

    private lateinit var binding : ActivitySignUpBinding

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
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

        binding.btnSignUp2.setOnClickListener {
            signUpUser()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignUp)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }

        binding.toolbarSignUp.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun signUpUser() {
        val name : String = binding.etName.text.toString().trim { it <= ' '}
        val email : String = binding.etEmail1.text.toString().trim {it <= ' '}
        val password : String = binding.etPassword1.text.toString().trim {it <= ' '}

        if (validateForm(name, email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)

                        FireStoreClass().registerUser(this@SignUpActivity, user)

                    } else {
                        hideProgressDialog()
                        val message = task.exception!!.message.toString()
                        showErrorSnackBar(message)
                    }

                }
        }
    }


    private fun validateForm(name : String, email : String, password : String) : Boolean{
        return when {
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
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

    fun userRegisteredSuccess() {
        Toast.makeText(this@SignUpActivity,
            "You have successfully registered as a new user",
            Toast.LENGTH_SHORT).show()

        hideProgressDialog()
        auth.signOut()
        finish()
    }

}