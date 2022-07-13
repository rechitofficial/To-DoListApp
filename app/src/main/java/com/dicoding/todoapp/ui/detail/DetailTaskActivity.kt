package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.ui.list.TaskViewModel
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID
import com.google.android.material.textfield.TextInputEditText

class DetailTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)


        //TODO 11 : Show detail task and implement delete action
        val factory = ViewModelFactory.getInstance(this)
        val detailTaskViewModel = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]

        val id: Int = intent.getIntExtra(TASK_ID, 0)

        // detail task
        detailTaskViewModel.setTaskId(id)
        detailTaskViewModel.task.observe(this) { task ->
            if (task != null){
                findViewById<TextInputEditText>(R.id.detail_ed_title).setText(task.title)
                findViewById<TextInputEditText>(R.id.detail_ed_description).setText(task.description)
                findViewById<EditText>(R.id.detail_ed_due_date).setText(DateConverter.convertMillisToString(task.dueDateMillis))
            }
        }

        // delete action
        findViewById<Button>(R.id.btn_delete_task).setOnClickListener {
            detailTaskViewModel.deleteTask()
            onBackPressed()
        }

    }
}