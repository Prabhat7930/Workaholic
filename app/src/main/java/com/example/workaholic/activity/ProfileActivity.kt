@file:Suppress("DEPRECATION")

package com.example.workaholic.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.workaholic.R
import com.example.workaholic.databinding.ActivityProfileBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.User
import java.io.IOException

class ProfileActivity : BaseActivity() {

    private lateinit var binding : ActivityProfileBinding

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        //private const val IMAGE_READ_REQUEST_CODE = 2
    }

    private var mySelectedImageURI : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        FireStoreClass().loadUserData(this@ProfileActivity)

        binding.userProfileImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES) ==
                PackageManager.PERMISSION_GRANTED) {
                //takeImagefromGallery()
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            else {
                ActivityCompat.requestPermissions(
                    this@ProfileActivity,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //takeImagefromGallery()
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
        else {
            Toast.makeText(this@ProfileActivity,
                "You denied the permission to read storage.", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {uri ->
        if (uri != null) {

            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            applicationContext.contentResolver.takePersistableUriPermission(uri, flag)

            mySelectedImageURI = uri

            try {
                Glide
                    .with(this@ProfileActivity)
                    .load(mySelectedImageURI)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.userProfileImage)
            }
            catch (e : IOException) {
                e.printStackTrace()
            }

        }
        else {
            Log.d("photo", "not selected")
        }
    }

    /*private fun takeImagefromGallery() {
        var galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galleryIntent)
    }*/


    /* LATEST NON DEPRECATED METHOD FOR IMAGE PICK
    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            mySelectedImageURI = data?.data


            try {
                Glide
                    .with(this@ProfileActivity)
                    .load(mySelectedImageURI)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.userProfileImage)
            }
            catch (e : IOException) {
                e.printStackTrace()
            }
        }
    }*/


    /* DEPRECATED FOR ACTIVITY RESULT
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == IMAGE_READ_REQUEST_CODE
            && data!!.data != null) {
            mySelectedImageURI = data.data


            try {
                Glide
                    .with(this@ProfileActivity)
                    .load(mySelectedImageURI)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.userProfileImage)
            }
            catch (e : IOException) {
                 e.printStackTrace()
            }


        }
    }*/

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProfile)

        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white)
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