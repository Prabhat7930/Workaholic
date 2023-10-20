@file:Suppress("DEPRECATION")

package com.example.workaholic.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.workaholic.R
import com.example.workaholic.databinding.DialogProgressBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

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
        val prgDialogText = myProgressDialog.findViewById<TextView>(R.id.tv_progress_text)
        prgDialogText.text = text
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

        Handler().postDelayed({
            doubleBackToExitPressOnce = false
        }, 2000)
    }

     fun showErrorSnackBar(message : String) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content),
            message, Snackbar.LENGTH_SHORT)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,
            R.color.snackbar_error_color))

        snackBar.show()
    }
}