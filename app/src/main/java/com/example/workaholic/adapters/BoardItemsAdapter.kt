package com.example.workaholic.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.workaholic.R
import com.example.workaholic.models.Board

open class BoardItemsAdapter(private val context : Context,
    private var list : ArrayList<Board>,
    /*private var userList : ArrayList<User>*/) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener : OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_board,
                parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_board_list_image))

            holder.itemView.findViewById<TextView>(R.id.tv_board_name).text = model.name
            var creator = "Created by : ${model.createdBy}"
            holder.itemView.findViewById<TextView>(R.id.tv_board_created_by).text = creator

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onCLick(position, model)
                }
            }
        }
    }


    interface OnClickListener {
        fun onCLick(position: Int, model : Board)
    }

    fun setOnClickListener(onClickListener : OnClickListener) {
        this.onClickListener = onClickListener
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}