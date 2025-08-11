package com.example.uniquetodolist;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements
        AddEditTaskDialog.AddEditTaskListener,
        TaskAdapter.OnTaskClickListener {

    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this);
        tasksRecyclerView.setAdapter(taskAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showAddTaskDialog());

        loadTasks();
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(databaseHelper.getAllTasks());
        taskAdapter.notifyDataSetChanged();
    }

    private void showAddTaskDialog() {
        AddEditTaskDialog dialog = AddEditTaskDialog.newInstanceAdd();
        dialog.show(getSupportFragmentManager(), "AddEditTaskDialog");
    }

    private void showEditTaskDialog(Task task) {
        AddEditTaskDialog dialog = AddEditTaskDialog.newInstanceEdit(task);
        dialog.show(getSupportFragmentManager(), "AddEditTaskDialog");
    }

    @Override
    public void onTaskAdded(Task newTask) {
        databaseHelper.addTask(newTask);
        loadTasks();
    }

    @Override
    public void onTaskUpdated(Task updatedTask) {
        databaseHelper.updateTask(updatedTask);
        loadTasks();
    }

    @Override
    public void onTaskClick(int position) {
        Task task = taskList.get(position);
        showEditTaskDialog(task);
    }

    @Override
    public void onTaskLongClick(int position) {
        Task task = taskList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteTask(task.getId());
                    loadTasks();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onCheckboxClick(int position, boolean isChecked) {
        Task task = taskList.get(position);
        task.setCompleted(isChecked);
        databaseHelper.updateTask(task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete_all) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete All Tasks")
                    .setMessage("Are you sure you want to delete ALL tasks?")
                    .setPositiveButton("Delete All", (dialog, which) -> {
                        for (Task task : taskList) {
                            databaseHelper.deleteTask(task.getId());
                        }
                        loadTasks();
                        Toast.makeText(this, "All tasks deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}