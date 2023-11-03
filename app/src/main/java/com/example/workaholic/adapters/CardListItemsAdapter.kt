package com.example.workaholic.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workaholic.R
import com.example.workaholic.activity.TaskListActivity
import com.example.workaholic.models.Cards
import com.example.workaholic.models.SelectedMembers

open class CardListItemsAdapter(
    private val context : Context,
    private val list : ArrayList<Cards>
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_card,
                parent,
                false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, cardPosition: Int) {
        val model = list[cardPosition]

        if (holder is MyViewHolder) {
            holder.itemView.findViewById<TextView>(R.id.tv_card_name).text = model.name

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(cardPosition)
                }
            }
        }
        if (model.color.isNotEmpty()) {
            holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.VISIBLE
            holder.itemView.findViewById<View>(R.id.view_label_color).setBackgroundColor(Color.parseColor(model.color))
        }
        else {
            holder.itemView.findViewById<View>(R.id.view_label_color).visibility = View.GONE
        }

        if ((context as TaskListActivity).myAssignedMemberDetailList.size > 0) {
            val selectedMembersList : ArrayList<SelectedMembers> = ArrayList()

            for (i in context.myAssignedMemberDetailList.indices) {
                for (j in model.assignedTo) {
                    if (context.myAssignedMemberDetailList[i].id == j) {
                        val selectedMember = SelectedMembers(
                            context.myAssignedMemberDetailList[i].id,
                            context.myAssignedMemberDetailList[i].image
                        )
                        selectedMembersList.add(selectedMember)
                    }
                }
            }
            if (selectedMembersList.size > 0) {
                holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_member_list).visibility = View.VISIBLE

                holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_member_list).layoutManager = GridLayoutManager(context, 4)
                val adapter = CardMembersListItemAdapter(context, selectedMembersList, false)
                holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_member_list).adapter = adapter

                adapter.setOnClickListener(object : CardMembersListItemAdapter.OnClickListener {
                    override fun onClick() {
                        if (onClickListener != null) {
                            onClickListener!!.onClick(holder.adapterPosition)
                        }
                    }

                })
            }
            else {
                holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_member_list).visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(cardPosition)
            }
        }
    }

    fun setOnClickListener(onClickListener : OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(cardPosition: Int)
    }

    class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)

}
