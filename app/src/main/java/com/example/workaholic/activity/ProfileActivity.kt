@file:Suppress("DEPRECATION")

package com.example.workaholic.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
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
import com.example.workaholic.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class ProfileActivity : BaseActivity() {

    private lateinit var binding : ActivityProfileBinding

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
    }

    private var mySelectedImageURI : Uri? = null
    private var myProfileImageURL : String = ""
    private lateinit var myUserDetails : User

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
                //takeImageFromGallery()
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

        binding.btnUpdate.setOnClickListener {
            if (mySelectedImageURI != null) {
                uploadUserImage(myUserDetails)
            }
            else {
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProfile)

        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        binding.toolbarProfile.setNavigationOnClickListener {onBackPressed() }
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
                //takeImageFromGallery()
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

    /*private fun takeImageFromGallery() {
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
    fun setUserDataInUI (user : User) {

        myUserDetails = user

        Glide
            .with(this@ProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.userProfileImage)

        binding.tvName2.setText(user.name)
        binding.tvEmail3.setText(user.email)
        if (user.mobileNum != 0L && user.mobileNum.toString().length == 10) {
            binding.tvMobile.setText(user.mobileNum.toString())
        }
    }

    private fun uploadUserImage(user: User) {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mySelectedImageURI != null) {
            val storageRef : StorageReference = FirebaseStorage.getInstance()
                .reference.child("${user.id}.${getFileExtension(mySelectedImageURI)}")

            storageRef.putFile(mySelectedImageURI!!).addOnSuccessListener {
                taskSnapshot ->
                hideProgressDialog()
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    myProfileImageURL = uri.toString()
                    
                    updateUserProfileData()
                }
            }.addOnFailureListener {
                exception ->
                Toast.makeText(this@ProfileActivity,
                    exception.message, Toast.LENGTH_SHORT).show()

                hideProgressDialog()
            }

        }
    }

    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()
        var changesMade = false

        if (myProfileImageURL.isNotEmpty() && myProfileImageURL != myUserDetails.image) {
            userHashMap[Constants.IMAGE] = myProfileImageURL
            changesMade = true
        }

        if (binding.tvName2.text.toString().isNotEmpty() && binding.tvName2.text.toString() != myUserDetails.name) {
            userHashMap[Constants.NAME] = binding.tvName2.text.toString()
            changesMade = true
        }
        else if (binding.tvName2.text.toString().isEmpty()){
            hideProgressDialog()
            changesMade = false
            Toast.makeText(this@ProfileActivity, "Name cannot be null", Toast.LENGTH_SHORT).show()
        }

        if (binding.tvMobile.text.toString().isNotEmpty() && binding.tvMobile.text.toString() != myUserDetails.mobileNum.toString()
            && binding.tvMobile.text.toString().length == 10 && binding.tvMobile.text.toString().toLong() != 0L) {
            userHashMap[Constants.MOBILE] = binding.tvMobile.text.toString().toLong()
            changesMade = true
        }
        else if (binding.tvMobile.text.toString().isEmpty() || binding.tvMobile.text.toString().length != 10
            || binding.tvMobile.text.toString().toLong() == 0L) {
            hideProgressDialog()
            changesMade = false
            Toast.makeText(this@ProfileActivity, "Mobile number cannot be left empty", Toast.LENGTH_SHORT).show()
        }

        if (changesMade) {
            FireStoreClass().updateUserProfileData(this@ProfileActivity,
                userHashMap)
        }
    }

    private fun getFileExtension(uri : Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(
            contentResolver.getType(uri!!)
        )
    }
    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)

        finish()
    }
}