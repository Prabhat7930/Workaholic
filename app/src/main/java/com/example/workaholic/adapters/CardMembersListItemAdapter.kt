package com.example.workaholic.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.example.workaholic.R
import com.example.workaholic.models.SelectedMembers
import de.hdodenhof.circleimageview.CircleImageView

open class CardMembersListItemAdapter(
    private val context : Context,
    private val list : ArrayList<SelectedMembers>,
    private val assignMember : Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener : OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_card_selected_member, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            if (position == list.size - 1 && assignMember) {
                holder.itemView.findViewById<CircleImageView>(R.id.iv_add_member).visibility = View.VISIBLE
                holder.itemView.findViewById<CircleImageView>(R.id.iv_selected_member_image).visibility = View.GONE
            }
            else {
                holder.itemView.findViewById<CircleImageView>(R.id.iv_add_member).visibility = View.GONE
                holder.itemView.findViewById<CircleImageView>(R.id.iv_selected_member_image).visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.findViewById(R.id.iv_selected_member_image))
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick()
                }
            }
        }
    }

    class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)

    fun setOnClickListener(onClickListener : OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick()
    }
}