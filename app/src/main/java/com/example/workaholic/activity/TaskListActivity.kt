package com.example.workaholic.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workaholic.R
import com.example.workaholic.adapters.TaskListItemsAdapter
import com.example.workaholic.databinding.ActivityTaskListBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.Board
import com.example.workaholic.models.Cards
import com.example.workaholic.models.Task
import com.example.workaholic.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var binding : ActivityTaskListBinding

    private lateinit var myBoardDetails : Board
    private lateinit var boardDocumentId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        }

        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this@TaskListActivity, boardDocumentId)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarTask)

        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white)
            actionBar.title = myBoardDetails.name
        }

        binding.toolbarTask.setNavigationOnClickListener {onBackPressedDispatcher.onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_members -> {
                val intent = Intent(this@TaskListActivity, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, myBoardDetails)
                startActivity(intent)
            }
        }
        return true
    }

    fun boardDetails(board : Board) {
        myBoardDetails = board
        hideProgressDialog()
        setupActionBar()

        val addTaskList = Task(resources.getString(R.string.add_task))
        board.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)

        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this@TaskListActivity,
            board.taskList)
        binding.rvTaskList.adapter = adapter
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

    fun updateTaskList(position : Int, listName : String, model : Task) {
        val task = Task(listName, model.createdBy)

        myBoardDetails.taskList[position] = task
        myBoardDetails.taskList.removeAt(myBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@TaskListActivity, myBoardDetails)
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this@TaskListActivity, myBoardDetails.documentId)
    }

    fun deleteTaskList(position: Int) {
        myBoardDetails.taskList.removeAt(position)
        myBoardDetails.taskList.removeAt(myBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@TaskListActivity, myBoardDetails)
    }

    fun addCardToTaskList(position: Int, cardName : String) {
        myBoardDetails.taskList.removeAt(myBoardDetails.taskList.size - 1)

        val cardAssignedUserList : ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FireStoreClass().getCurrUserId())

        val card = Cards(
            cardName,
            FireStoreClass().getCurrUserId(),
            cardAssignedUserList
        )

        val cardList = myBoardDetails.taskList[position].cards
        cardList.add(card)

        val task = Task(
            myBoardDetails.taskList[position].title,
            myBoardDetails.taskList[position].createdBy,
            cardList
        )

        myBoardDetails.taskList[position] = task

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@TaskListActivity, myBoardDetails)
    }

}