package com.example.workaholic.activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.workaholic.R
import com.example.workaholic.databinding.ActivityCardDetailsBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.Board
import com.example.workaholic.models.Cards
import com.example.workaholic.models.Task
import com.example.workaholic.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private lateinit var binding : ActivityCardDetailsBinding

    private lateinit var myBoardDetails : Board
    private var taskListPosition : Int = -1
    private var cardPosition : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        }

        getStringInfo()
        setupActionBar()

        binding.etCardNameDetails.setText(myBoardDetails.taskList[taskListPosition].cards[cardPosition].name)


    }

    private fun getStringInfo() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            myBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            taskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            cardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCardDetail)

        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white)
            actionBar.title = myBoardDetails.taskList[taskListPosition].cards[cardPosition].name
        }

        binding.toolbarCardDetail.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btnUpdateCard.setOnClickListener {
            if (binding.etCardNameDetails.text!!.isNotEmpty()) {
                updateCardDetails()
            }
            else {
                Toast.makeText(this, "Card name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_card_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_card -> {
                alertDialogForCardDeletion()
            }
        }
        return true
    }

    private fun updateCardDetails() {
        val card = Cards(
            binding.etCardNameDetails.text.toString(),
            myBoardDetails.taskList[taskListPosition].cards[cardPosition].createdBy,
            myBoardDetails.taskList[taskListPosition].cards[cardPosition].assignedTo
        )

        myBoardDetails.taskList[taskListPosition].cards[cardPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, myBoardDetails)
    }

    private fun deleteCard() {
        val cardList : ArrayList<Cards> = myBoardDetails.taskList[taskListPosition].cards

        cardList.removeAt(cardPosition)

        val taskList : ArrayList<Task> = myBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[taskListPosition].cards = cardList

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, myBoardDetails)
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun alertDialogForCardDeletion() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")

        builder.setMessage("Are you sure you want to delete ${myBoardDetails.taskList[taskListPosition].cards[cardPosition].name}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()
        }

        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}