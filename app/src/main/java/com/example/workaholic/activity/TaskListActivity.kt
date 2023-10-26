package com.example.workaholic.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workaholic.R
import com.example.workaholic.adapters.TaskListItemsAdapter
import com.example.workaholic.databinding.ActivityTaskListBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.Board
import com.example.workaholic.models.Task
import com.example.workaholic.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var binding : ActivityTaskListBinding

    private lateinit var myBoardDetails : Board
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


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarTask)

        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white)
            actionBar.title = myBoardDetails.name
        }

        binding.toolbarTask.setNavigationOnClickListener {onBackPressed() }
    }
    fun boardDetails(board : Board) {
        myBoardDetails = board
        hideProgressDialog()
        setupActionBar()

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)

        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this@TaskListActivity,
            board.taskList)
        binding.rvTaskList.adapter = adapter
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this@TaskListActivity, myBoardDetails.documentId)
    }

    fun createTaskList(taskListName : String) {
        val task = Task(
            taskListName,
            FireStoreClass().getCurrUserId()
        )

        myBoardDetails.taskList.add(0, task)
        myBoardDetails.taskList.removeAt(myBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@TaskListActivity, myBoardDetails)
    }



}