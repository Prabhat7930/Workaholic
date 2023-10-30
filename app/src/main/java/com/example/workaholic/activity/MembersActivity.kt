package com.example.workaholic.activity

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workaholic.R
import com.example.workaholic.adapters.MemberListItemAdapter
import com.example.workaholic.databinding.ActivityMembersBinding
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.models.Board
import com.example.workaholic.models.User
import com.example.workaholic.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var binding : ActivityMembersBinding

    private lateinit var myBoardDetails : Board
    private lateinit var myAssignedMembersList : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        }

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            @Suppress("DEPRECATION")
            myBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAssignedMembersListDetails(this, myBoardDetails.assignedTo)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMember)

        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white)
            actionBar.title = resources.getString(R.string.members)
        }

        binding.toolbarMember.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_add_members -> {
                dialogToSearchMembers()
            }
        }
        return true
    }

    fun setupMemberList(list : ArrayList<User>) {
        myAssignedMembersList = list
        hideProgressDialog()

        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemAdapter(this, list)
        binding.rvMembersList.adapter = adapter
    }

    fun memberDetails(user : User) {
        myBoardDetails.assignedTo.add(user.id)
        FireStoreClass().assignMemberToBoard(this@MembersActivity, myBoardDetails, user)
    }

    fun memberAssignedSuccess(user : User) {
        hideProgressDialog()
        myAssignedMembersList.add(user)
        setupMemberList(myAssignedMembersList)
    }

    private fun dialogToSearchMembers() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_search_email).text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMembersDetails(this@MembersActivity, email)
            }
            else {
                Toast.makeText(this, "Please enter a email to search", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }
}