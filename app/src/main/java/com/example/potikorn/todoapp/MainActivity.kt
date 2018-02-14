package com.example.potikorn.todoapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.objectbox.Box
import io.objectbox.android.AndroidScheduler
import io.objectbox.query.Query
import io.objectbox.reactive.DataSubscription
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_todo.view.*
import kotlinx.android.synthetic.main.layout_add_todo.*
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var todoBox: Box<TodoModel>
    private lateinit var todoQuery: Query<TodoModel>
    private lateinit var subscription: DataSubscription

    private val todoAdapter: TodoAdapter by lazy { TodoAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todoBox = (application as ToDoApplication).boxStore.boxFor(TodoModel::class.java)
        todoQuery = todoBox.query().build()
        subscription = todoQuery.subscribe()
                .on(AndroidScheduler.mainThread())
                .observer { todoList -> todoAdapter.items = todoList }

        fabAdd.setOnClickListener {
            val dialog = Dialog(this)
            dialog.apply {
                setContentView(R.layout.layout_add_todo)
                btnSave.setOnClickListener {
                    todoBox.put(TodoModel(
                            topic = etTopic.text.toString(),
                            detail = etDetail.text.toString(),
                            date = Calendar.getInstance().time
                    ))
                    this.dismiss()
                }
                show()
            }
        }

        rvTodoLists.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = todoAdapter
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val enable = s.isNotEmpty()
                btnSearch.isEnabled = enable
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })

        btnSearch.setOnClickListener {
            todoAdapter.items = todoBox.query()
                    .contains(TodoModel_.topic, etSearch.text.trim().toString())
                    .build().find()
        }
    }

    override fun onDestroy() {
        subscription.cancel()
        super.onDestroy()
    }

    inner class TodoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var items: MutableList<TodoModel> by Delegates.observable(mutableListOf(), { _, _, _ -> notifyDataSetChanged() })

        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.item_todo, parent, false)
            return TodoViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            return (holder as TodoViewHolder).onBindData(items[position])
        }

        inner class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            @SuppressLint("SetTextI18n")
            fun onBindData(todoModel: TodoModel) {
                itemView.tvTopic.text = "${todoModel.id} : ${todoModel.topic}"
                itemView.tvDetail.text = todoModel.detail
                itemView.tvDate.text = todoModel.date.toString()
                itemView.setOnClickListener {
                    //TODO when touch go to do something
                }
                itemView.setOnLongClickListener {
                    AlertDialog.Builder(this@MainActivity).apply {
                        setTitle(getString(R.string.confirm_delete))
                        setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            todoBox.remove(todoModel.id)
                            dialog.dismiss()
                        }
                        setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        create()
                        show()
                    }
                    return@setOnLongClickListener true
                }
            }
        }
    }
}

