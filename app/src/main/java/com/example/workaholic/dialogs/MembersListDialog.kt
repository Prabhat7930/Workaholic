package com.example.workaholic.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workaholic.R
import com.example.workaholic.adapters.MemberListItemAdapter
import com.example.workaholic.models.User

abstract class MembersListDialog(
    context: Context,
    private var list : ArrayList<User>,
    private var title : String = ""
) : Dialog(context){

    private var adapter : MemberListItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        val view = LayoutInflater.from(context).inflate(R.layout.member_dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view : View) {
        view.findViewById<TextView>(R.id.tv_dialog_title3).text = title

        if (list.size > 0) {
            view.findViewById<RecyclerView>(R.id.rv_members_list2).layoutManager = LinearLayoutManager(context)
            adapter = MemberListItemAdapter(context, list)
            view.findViewById<RecyclerView>(R.id.rv_members_list2).adapter = adapter

            adapter!!.setOnClickListener(object : MemberListItemAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action: String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            })
        }
    }

    protected abstract fun onItemSelected(user : User, action : String)
}