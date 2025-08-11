package com.example.uniquetodolist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddEditTaskDialog extends DialogFragment {
    private EditText titleEditText;
    private EditText descriptionEditText;
    private AddEditTaskListener listener;
    private Task taskToEdit;

    public interface AddEditTaskListener {
        void onTaskAdded(Task newTask);
        void onTaskUpdated(Task updatedTask);
    }

    public static AddEditTaskDialog newInstanceAdd() {
        return new AddEditTaskDialog();
    }

    public static AddEditTaskDialog newInstanceEdit(Task task) {
        AddEditTaskDialog dialog = new AddEditTaskDialog();
        Bundle args = new Bundle();
        args.putSerializable("task", task);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskToEdit = (Task) getArguments().getSerializable("task");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_edit_task, null);

        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);

        if (taskToEdit != null) {
            titleEditText.setText(taskToEdit.getTitle());
            descriptionEditText.setText(taskToEdit.getDescription());
            builder.setTitle("Edit Task");
        } else {
            builder.setTitle("Add New Task");
        }

        builder.setView(view)
                .setPositiveButton(taskToEdit != null ? "Update" : "Add", (dialog, which) -> {
                    String title = titleEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();

                    if (taskToEdit != null) {
                        taskToEdit.setTitle(title);
                        taskToEdit.setDescription(description);
                        listener.onTaskUpdated(taskToEdit);
                    } else {
                        Task newTask = new Task(title, description);
                        listener.onTaskAdded(newTask);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddEditTaskListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddEditTaskListener");
        }
    }
}