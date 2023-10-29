package com.example.workaholic.activity

import android.os.Build
import android.os.Bundle
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

    fun setupMemberList(list : ArrayList<User>) {
        hideProgressDialog()

        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemAdapter(this, list)
        binding.rvMembersList.adapter = adapter
    }
}