package com.example.workaholic.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.workaholic.R
import com.example.workaholic.activity.TaskListActivity
import com.example.workaholic.models.Task

open class TaskListItemsAdapter(private val context : Context,
    private var list : ArrayList<Task>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.8).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(
            (15.toDp().toPx()), 0, (40.toDp().toPx()), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            if (position == list.size - 1) {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task).visibility = View.VISIBLE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.GONE
            }
            else {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task).visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.VISIBLE
            }
            holder.itemView.findViewById<TextView>(R.id.tv_task_list_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_add_task).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list).visibility = View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ibtn_close_list).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list).visibility = View.GONE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ibtn_done_list).setOnClickListener {
                val listName = holder.itemView.findViewById<EditText>(R.id.et_task_name).text.toString()
                
                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                }
                else {
                    Toast.makeText(context, "Please Enter List Name ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun Int.toDp() : Int = (this / Resources.getSystem().displayMetrics.density.toInt())

    private fun Int.toPx() : Int = (this * Resources.getSystem().displayMetrics.density.toInt())

    class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)

}