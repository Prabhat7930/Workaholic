package com.example.workaholic.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workaholic.R
import com.example.workaholic.activity.TaskListActivity
import com.example.workaholic.models.Task
import java.util.Collections

open class TaskListItemsAdapter(private val context : Context,
    private var list : ArrayList<Task>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var myPositionDraggedFrom = -1
    private var myPositionDraggedTo = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.9).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(
            (15.toDp().toPx()), 0, (40.toDp().toPx()), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, taskPosition : Int) {
        val model = list[taskPosition]
        if (holder is MyViewHolder) {
            if (taskPosition == list.size - 1) {
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
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ibtn_close_list).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.GONE
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

            holder.itemView.findViewById<ImageButton>(R.id.ibtn_edit_list_name).setOnClickListener {
                holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).setText(model.title)
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list).visibility = View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ibtn_close_edit_view).setOnClickListener {
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list).visibility = View.GONE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ibtn_done_edit_list_name).setOnClickListener {
                val listName = holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).text.toString()

                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.updateTaskList(taskPosition, listName, model)
                    }
                }
                else {
                    Toast.makeText(context, "Please enter task name", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.ibtn_delete_list).setOnClickListener {
                alertDialogForListDeletion(taskPosition, model.title)
            }

            //holder.itemView.findViewById<ScrollView>(R.id.sl_card_list).fullScroll(View.FOCUS_DOWN)
            //holder.itemView.findViewById<ScrollView>(R.id.sl_card_list).smooth

            holder.itemView.findViewById<TextView>(R.id.tv_add_card).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ibtn_done_card_name).setOnClickListener {
                val cardName = holder.itemView.findViewById<EditText>(R.id.et_card_name).text.toString()

                if (cardName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.addCardToTaskList(taskPosition, cardName)
                    }
                }
                else {
                    Toast.makeText(context, "Please Enter Card Name", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.ibtn_close_card_name).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.GONE
            }

            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).layoutManager = LinearLayoutManager(context)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).setHasFixedSize(true)

            val adapter = CardListItemsAdapter(context, model.cards)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).adapter = adapter

            adapter.setOnClickListener(object : CardListItemsAdapter.OnClickListener {
                override fun onClick(cardPosition: Int) {
                    if (context is TaskListActivity) {
                        context.cardDetails(holder.adapterPosition, cardPosition)
                    }
                }
            })

            val dividerItemDecoration = DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL)

            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).addItemDecoration(dividerItemDecoration)
            val helper = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        dragged: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val draggedPosition = dragged.adapterPosition
                        val targetPosition = target.adapterPosition

                        if (myPositionDraggedFrom == -1) {
                            myPositionDraggedFrom = draggedPosition
                        }
                        myPositionDraggedTo = targetPosition

                        Collections.swap(list[taskPosition].cards, draggedPosition, targetPosition)

                        adapter.notifyItemMoved(draggedPosition, targetPosition)
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    }

                    override fun clearView(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ) {
                        super.clearView(recyclerView, viewHolder)
                        if (myPositionDraggedFrom != -1 && myPositionDraggedTo != -1 &&
                            myPositionDraggedFrom != myPositionDraggedTo) {
                            (context as TaskListActivity).updateCardsInTaskList(
                                taskPosition,
                                list[taskPosition].cards
                            )
                        }

                        myPositionDraggedFrom = -1
                        myPositionDraggedTo = -1
                    }

                }
            )
            helper.attachToRecyclerView(holder.itemView.findViewById(R.id.rv_card_list))
        }
    }

    private fun alertDialogForListDeletion(taskPosition : Int, title : String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")

        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(R.drawable.ic_alert)

        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()

            if (context is TaskListActivity) {
                context.deleteTaskList(taskPosition)
            }
        }

        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun Int.toDp() : Int = (this / Resources.getSystem().displayMetrics.density.toInt())

    private fun Int.toPx() : Int = (this * Resources.getSystem().displayMetrics.density.toInt())

    class MyViewHolder(view : View) : RecyclerView.ViewHolder(view)

}