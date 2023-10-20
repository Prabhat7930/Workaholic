package com.example.workaholic.activity

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.workaholic.R
import com.example.workaholic.databinding.ActivityProfileBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.User

class ProfileActivity : BaseActivity() {

    private lateinit var binding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        FireStoreClass().loadUserData(this@ProfileActivity)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProfile)

        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        binding.toolbarProfile.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    fun setUserDataInUI (user : User) {
        Glide
            .with(this@ProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.userProfileImage)

        binding.tvName2.setText(user.name)
        binding.tvEmail3.setText(user.email)
        if (user.mobileNum != 0L && user.mobileNum.toString().length > 9) {
            binding.tvMobile.setText(user.mobileNum.toString())
        }
    }
}