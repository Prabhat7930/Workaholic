package com.example.workaholic.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.workaholic.R
import com.example.workaholic.databinding.ActivityMainBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.User
import com.google.android.material.navigation.NavigationView.*
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.navView.setNavigationItemSelectedListener (this)

        FireStoreClass().loadUserData(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.appBar.toolbarMain)
        binding.appBar.toolbarMain.setNavigationIcon(R.drawable.baseline_menu_24)

        binding.appBar.toolbarMain.setNavigationOnClickListener {
            toggleDrawer()
        }

    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_my_profile -> {
                Handler().postDelayed({
                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                }, 300)

            }
            R.id.nav_sign_out -> {

                Handler().postDelayed({
                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }, 300)

            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    fun updateNavUserDetails(user : User) {
        val navUserImage : CircleImageView = findViewById(R.id.nav_user_image)
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)
    }


}