package com.example.workaholic.activity

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.workaholic.R
import com.example.workaholic.adapters.CardMembersListItemAdapter
import com.example.workaholic.databinding.ActivityCardDetailsBinding
import com.example.workaholic.dialogs.LabelColorListDialog
import com.example.workaholic.dialogs.MembersListDialog
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.Board
import com.example.workaholic.models.Cards
import com.example.workaholic.models.SelectedMembers
import com.example.workaholic.models.Task
import com.example.workaholic.models.User
import com.example.workaholic.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private lateinit var binding : ActivityCardDetailsBinding

    private lateinit var myBoardDetails : Board
    private var taskListPosition : Int = -1
    private var cardPosition : Int = -1
    private var mySelectedColor : String = ""
    private lateinit var myMembersDetailList : ArrayList<User>

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

        mySelectedColor = myBoardDetails.taskList[taskListPosition].cards[cardPosition].color
        if (mySelectedColor.isNotEmpty()) {
            setColor()
        }

        binding.btnUpdateCard.setOnClickListener {
            if (binding.etCardNameDetails.text!!.isNotEmpty()) {
                updateCardDetails()
            }
            else {
                Toast.makeText(this, "Card name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLabelColor.setOnClickListener {
            labelColorListDialog()
        }

        binding.tvSelectMembers.setOnClickListener {
            membersListDialog()
        }

        setupSelectedMembersList()
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
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            myMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
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
            myBoardDetails.taskList[taskListPosition].cards[cardPosition].assignedTo,
            mySelectedColor
        )

        val taskList : ArrayList<Task> = myBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

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

    private fun colorsList() : ArrayList<String> {
        val colorList : ArrayList<String> = ArrayList()
        colorList.add("#0022f8")
        colorList.add("#4361ee")
        colorList.add("#ffd60a")
        colorList.add("#4f772d")
        colorList.add("#621708")
        colorList.add("#3d348b")
        colorList.add("#ff6700")

        return colorList
    }

    private fun setColor() {
        binding.tvLabelColor.text = ""
        binding.tvLabelColor.setBackgroundColor(Color.parseColor(mySelectedColor))
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

    private fun labelColorListDialog() {
        val colorsList : ArrayList<String> = colorsList()
        val listDialog = object : LabelColorListDialog(
            this@CardDetailsActivity,
            colorsList,
            resources.getString(R.string.select_color),
            mySelectedColor
        ) {
            override fun onItemSelected(color: String) {
                mySelectedColor = color
                setColor()
            }

        }
        listDialog.show()
    }

    private fun membersListDialog() {
        val cardAssignedMembersList = myBoardDetails.taskList[taskListPosition].cards[cardPosition].assignedTo

        if (cardAssignedMembersList.size > 0) {
            for (i in myMembersDetailList.indices) {
                for (j in cardAssignedMembersList) {
                    if (myMembersDetailList[i].id == j) {
                        myMembersDetailList[i].selected = true
                    }
                }
            }
        }
        else {
            for (i in myMembersDetailList.indices) {
                myMembersDetailList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
            this@CardDetailsActivity,
            myMembersDetailList,
            resources.getString(R.string.select_members)
        ) {
            override fun onItemSelected(user : User, action : String) {
                if (action == Constants.SELECT) {
                    if (! myBoardDetails.taskList[taskListPosition].cards[cardPosition].assignedTo.contains(user.id)) {
                        myBoardDetails.taskList[taskListPosition].cards[cardPosition].assignedTo.add(user.id)
                    }
                }
                else {
                    myBoardDetails.taskList[taskListPosition].cards[cardPosition].assignedTo.remove(user.id)

                    for (i in myMembersDetailList.indices) {
                        if (myMembersDetailList[i].id == user.id) {
                            myMembersDetailList[i].selected = false
                        }
                    }
                }
                setupSelectedMembersList()
            }
        }
        listDialog.show()
    }

    private fun setupSelectedMembersList() {
        val cardAssignedMembersList = myBoardDetails.taskList[taskListPosition].cards[cardPosition].assignedTo

        val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()

        for (i in myMembersDetailList.indices) {
            for (j in cardAssignedMembersList) {
                if (myMembersDetailList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        myMembersDetailList[i].id,
                        myMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("", ""))
            binding.tvSelectMembers.visibility = View.GONE
            binding.rvSelectedMembersList.visibility = View.VISIBLE

            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(
                this@CardDetailsActivity, 6
            )

            val adapter = CardMembersListItemAdapter(this@CardDetailsActivity, selectedMembersList, true)
            binding.rvSelectedMembersList.adapter = adapter

            adapter.setOnClickListener(object : CardMembersListItemAdapter.OnClickListener {
                override fun onClick() {
                    membersListDialog()
                }
            })
        }
        else {
            binding.tvSelectMembers.visibility = View.VISIBLE
            binding.rvSelectedMembersList.visibility = View.GONE
        }
    }
}