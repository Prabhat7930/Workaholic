package com.example.workaholic.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import com.example.workaholic.databinding.ActivityBoardBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.Board
import com.example.workaholic.models.User
import com.example.workaholic.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class BoardActivity : BaseActivity() {

    private lateinit var binding : ActivityBoardBinding
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
    }

    private var mySelectedImageURI : Uri? = null
    private var myBoardImageURL : String = ""
    private lateinit var myUserDetails : User
    private lateinit var myUserName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        }

        setupActionBar()

        FireStoreClass().loadUserData(this@BoardActivity)

        if (intent.hasExtra(Constants.NAME)) {
            myUserName = intent.getStringExtra(Constants.NAME)!!
        }

        binding.ivBoardImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED) {
                    //takeImageFromGallery()
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                else {
                    ActivityCompat.requestPermissions(
                        this@BoardActivity,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        READ_STORAGE_PERMISSION_CODE
                    )
                }
            }
            else {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                    //takeImageFromGallery()
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                else {
                    ActivityCompat.requestPermissions(
                        this@BoardActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_STORAGE_PERMISSION_CODE
                    )
                }
            }
        }

        binding.btnCreateBoard.setOnClickListener {
            if (mySelectedImageURI != null) {
                uploadBoardImage(myUserDetails)
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarBoardActivity)

        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        binding.toolbarBoardActivity.setNavigationOnClickListener {onBackPressedDispatcher.onBackPressed()}
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
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
        else {
            Toast.makeText(this@BoardActivity,
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
                    .with(this@BoardActivity)
                    .load(mySelectedImageURI)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivBoardImage)
            }
            catch (e : IOException) {
                e.printStackTrace()
            }

        }
        else {
            Log.d("Image", "Not selected")
        }
    }

    private fun uploadBoardImage(user : User) {
        showProgressDialog(resources.getString(R.string.please_wait))

        val storageRef : StorageReference = FirebaseStorage.getInstance()
            .reference.child("${user.id}.${getFileExtension(mySelectedImageURI)}")

        storageRef.putFile(mySelectedImageURI!!)
            .addOnSuccessListener { taskSnapshot ->
                hideProgressDialog()
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        myBoardImageURL = uri.toString()

                        createBoard()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@BoardActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
            }
    }

    private fun createBoard() {
        val assignedUsersArrayList: ArrayList<String> = ArrayList()

        if (binding.etBoardName.text.toString() != "") {
            assignedUsersArrayList.add(getCurrentUserID())

            val board = Board(
                binding.etBoardName.text.toString(),
                myBoardImageURL,
                myUserName,
                assignedUsersArrayList
            )

            FireStoreClass().registerBoard(this@BoardActivity, board)
        }
        else {
            hideProgressDialog()
            Toast.makeText(this@BoardActivity, "Board Name cannot be empty!", Toast.LENGTH_SHORT).show()
        }
    }

    fun setupForUser(user : User) {
        myUserDetails = user
    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun getFileExtension(uri : Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(
            contentResolver.getType(uri!!)
        )
    }
}