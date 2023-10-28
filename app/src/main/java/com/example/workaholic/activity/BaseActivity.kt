@file:Suppress("DEPRECATION")

package com.example.workaholic.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.workaholic.R
import com.example.workaholic.databinding.DialogProgressBinding
import com.example.workaholic.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class BaseActivity : AppCompatActivity() {

    private lateinit var binding : DialogProgressBinding

    private var doubleBackToExitPressOnce = false

    private lateinit var myProgressDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun showProgressDialog(text : String) {

        myProgressDialog = Dialog(this)

        myProgressDialog.setContentView(R.layout.dialog_progress)
        myProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text
        myProgressDialog.show()
    }

    fun hideProgressDialog() {
        myProgressDialog.dismiss()
    }

    fun getCurrentUserID() : String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressOnce = true
        Toast.makeText(this,
            resources.getString(R.string.please_click_again_to_exit),
            Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressOnce = false
        }, 2000)
    }

     fun showErrorSnackBar(message : String) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content),
            message, Snackbar.LENGTH_SHORT)

         snackBar.view.setBackgroundColor(ContextCompat.getColor(this,
            R.color.snackbar_error_color))

        snackBar.show()
    }
}