package tech.snehil.tickoff.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

import tech.snehil.tickoff.AddNewTask;
import tech.snehil.tickoff.MainActivity;
import tech.snehil.tickoff.Model.ToDoModel;
import tech.snehil.tickoff.R;
import tech.snehil.tickoff.Utils.DatabaseHandler;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private MainActivity activity;
    private DatabaseHandler db;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity){
        this.activity = activity;
        this.db = db;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);

        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        db.openDatabase();
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(item.getStatus() != 0);
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.d("Check", String.valueOf(isChecked));
                if(compoundButton.isPressed()){
                    if(isChecked){
                        db.updateStatus(item.getId(), 1);
                    } else {
                        db.updateStatus(item.getId(), 0);
                    }
                }

                updateProgress();
            }
        });
    }

    public int getItemCount(){
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<ToDoModel> todoList){
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);

        updateProgress();
    }

    public void editItem(int position){
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    private void updateProgress(){
        List<ToDoModel> taskList = db.getAllTasks();

        int totalTasks = taskList.size();
        int completed = 0;
        for(ToDoModel task : taskList){
            if(task.getStatus() == 1){
                completed++;
            }
        }

        LinearProgressIndicator progressBar = activity.findViewById(R.id.progressBar);
        TextView progressPercent = activity.findViewById(R.id.progressPercentage);
        TextView progressFraction = activity.findViewById(R.id.progressFraction);

        if(totalTasks > 0) {
            progressBar.setProgressCompat(completed * 100 / totalTasks, true);
            progressPercent.setText(completed * 100 / totalTasks + "%");
            progressFraction.setText(completed + "/" + totalTasks);
        } else {
            progressBar.setProgressCompat(0, true);
            progressPercent.setText("No Tasks! Start by adding one.");
            progressFraction.setText("");
        }

        if(completed==totalTasks && totalTasks != 0){
            progressPercent.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
            progressFraction.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
        } else {
            progressPercent.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            progressFraction.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
        }
    }
}
