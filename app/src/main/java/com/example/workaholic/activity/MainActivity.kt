package com.example.workaholic.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View

import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.workaholic.R
import com.example.workaholic.adapters.BoardItemsAdapter
import com.example.workaholic.databinding.ActivityMainBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.Board
import com.example.workaholic.models.User
import com.example.workaholic.utils.Constants
import com.google.android.material.navigation.NavigationView.*
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainBinding

    private lateinit var myUserName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.navView.setNavigationItemSelectedListener (this)

        FireStoreClass().loadUserData(this, true)

        binding.appBar.btnBoardActivity.setOnClickListener {
            val intent = Intent(this@MainActivity, BoardActivity::class.java)
            intent.putExtra(Constants.NAME, myUserName)
            startUpdateBoardActivityForResult.launch(intent)
        }
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


    private val startUpdateActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    FireStoreClass().loadUserData(this@MainActivity)
                }
                else {
                    Log.e("Cancelled", "Cancelled")
                }
        }

    private val startUpdateBoardActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {
                FireStoreClass().getBoardList(this@MainActivity)
            }
            else {
                Log.e("Cancelled", "Cancelled")
            }
        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_my_profile -> {
                Handler().postDelayed({
                    startUpdateActivityForResult.launch(Intent(this@MainActivity, ProfileActivity::class.java))
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

    fun updateNavUserDetails(user : User, readBoardList : Boolean) {
        val navUserImage : CircleImageView = findViewById(R.id.nav_user_image)
        var navUserName : TextView = findViewById(R.id.tv_username)
        myUserName = user.name



        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        navUserName.text = user.name

        if (readBoardList) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardList(this@MainActivity)
        }
    }

    fun setupBoardListToUI(boardList: ArrayList<Board>) {
        hideProgressDialog()

        if (boardList.size > 0) {
            binding.appBar.mainContentForBoard.rvBoardList.visibility = View.VISIBLE
            binding.appBar.mainContentForBoard.tvNoBoard.visibility = View.GONE

            binding.appBar.mainContentForBoard.rvBoardList.layoutManager = LinearLayoutManager(this)
            binding.appBar.mainContentForBoard.rvBoardList.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardList)
            binding.appBar.mainContentForBoard.rvBoardList.adapter = adapter
        }
        else {
            binding.appBar.mainContentForBoard.rvBoardList.visibility = View.GONE
            binding.appBar.mainContentForBoard.tvNoBoard.visibility = View.VISIBLE
        }
    }


}