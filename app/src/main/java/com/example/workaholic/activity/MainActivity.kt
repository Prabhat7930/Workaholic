package com.example.workaholic.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : BaseActivity(), OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainBinding

    private lateinit var myUserName : String
    private lateinit var myUser: User
    private lateinit var mySharedPrefs : SharedPreferences

    companion object {
        const val POST_NOTIFICATION_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        }


        checkNotificationPermission()


        mySharedPrefs = this.getSharedPreferences(Constants.WORKAHOLIC_PREFERENCES, Context.MODE_PRIVATE)

        val tokenUpdated = mySharedPrefs.getBoolean(Constants.FCM_TOKEN_UPDATED, false)
        if (tokenUpdated) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().loadUserData(this@MainActivity, true)
        }
        else {
            FirebaseMessaging.getInstance().token.addOnSuccessListener{
                updateFCMToken(it)
            }
        }

        setupActionBar()

        binding.navView.setNavigationItemSelectedListener (this)

        FireStoreClass().loadUserData(this@MainActivity, true)

        binding.appBar.btnBoardActivity.setOnClickListener {
            val intent = Intent(this@MainActivity, BoardActivity::class.java)
            intent.putExtra(Constants.NAME, myUserName)
            startUpdateBoardActivityForResult.launch(intent)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.appBar.toolbarMain)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
        }

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

    private fun checkNotificationPermission(){
        if (ContextCompat.checkSelfPermission(this , Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission", "Granted")
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                POST_NOTIFICATION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == POST_NOTIFICATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Granted")
            }
            else {
                openAlertDialog()
            }
        }
    }

    private fun openAlertDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("This app requires your location to function!")
        alertDialogBuilder.setPositiveButton("Try again"
        ) { _, _ -> checkNotificationPermission() }
        alertDialogBuilder.setNegativeButton("Settings"
        ) { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            val packageName = "com.example.workaholic"
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this@MainActivity, "error!", Toast.LENGTH_SHORT).show()
            }
        }
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private val startUpdateActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    FireStoreClass().loadUserData(this@MainActivity)
                }
                else {
                    Log.e("Update Profile", "Cancelled")
                }
        }

    private val startUpdateBoardActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {
                FireStoreClass().getBoardList(this@MainActivity)
            }
            else {
                Log.e("Update Board", "Cancelled")
            }
        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_my_profile -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    startUpdateActivityForResult.launch(Intent(this@MainActivity, ProfileActivity::class.java))
                }, 300)

            }
            R.id.nav_sign_out -> {

                Handler(Looper.getMainLooper()).postDelayed({
                    FirebaseAuth.getInstance().signOut()
                    mySharedPrefs.edit().clear().apply()

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
        hideProgressDialog()
        myUser = user
        val navUserImage : CircleImageView = findViewById(R.id.nav_user_image)
        var navUserName : TextView = findViewById(R.id.tv_username)
        myUserName = user.name

        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        navUserName.text = myUserName

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


            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener {
                override fun onCLick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        }
        else {
            binding.appBar.mainContentForBoard.rvBoardList.visibility = View.GONE
            binding.appBar.mainContentForBoard.tvNoBoard.visibility = View.VISIBLE
        }
    }

    fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor : SharedPreferences.Editor = mySharedPrefs.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().loadUserData(this@MainActivity, true)
    }

    private fun updateFCMToken(token : String) {
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().updateUserProfileData(this@MainActivity, userHashMap)
    }
}