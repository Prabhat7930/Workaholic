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
import com.example.workaholic.adapters.LabelColorListItemAdapter

abstract class LabelColorListDialog(
    context : Context,
    private var list : ArrayList<String>,
    private val title : String = "",
    private var mySelectedColor : String = ""
) : Dialog(context) {

    private var adapter : LabelColorListItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.color_dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view : View) {
        view.findViewById<TextView>(R.id.tv_dialog_title2).text = title
        view.findViewById<RecyclerView>(R.id.rv_color_list).layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemAdapter(context, list, mySelectedColor)
        view.findViewById<RecyclerView>(R.id.rv_color_list).adapter = adapter

        adapter!!.onItemClickListener = object : LabelColorListItemAdapter.OnItemClickListener {
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }

        }
    }

    protected abstract fun onItemSelected(color : String)
}