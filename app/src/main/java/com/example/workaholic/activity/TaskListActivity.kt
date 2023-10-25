package com.example.workaholic.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.workaholic.R
import com.example.workaholic.databinding.ActivityTaskListBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.Board
import com.example.workaholic.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var binding : ActivityTaskListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var boardDocumentID = ""

        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this@TaskListActivity, boardDocumentID)
    }


    private fun setupActionBar(title : String) {
        setSupportActionBar(binding.toolbarTask)

        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white)
            actionBar.title = title
        }

        binding.toolbarTask.setNavigationOnClickListener {onBackPressed() }
    }
    fun boardDetails(board : Board) {
        hideProgressDialog()
        setupActionBar(board.name)
    }
}