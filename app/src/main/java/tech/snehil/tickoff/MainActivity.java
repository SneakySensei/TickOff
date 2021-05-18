package tech.snehil.tickoff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tech.snehil.tickoff.Adapter.ToDoAdapter;
import tech.snehil.tickoff.Model.ToDoModel;
import tech.snehil.tickoff.Utils.DatabaseHandler;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private LinearProgressIndicator progressBar;
    private TextView progressPercent;
    private TextView progressFraction;

    private List<ToDoModel> taskList ;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);
        db.openDatabase();

        taskList = new ArrayList<>();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tasksAdapter = new ToDoAdapter(db, this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        fab = findViewById(R.id.fab);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper((tasksAdapter)));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        taskList = db.getAllTasks();
//        Log.d("TASKS", taskList.toString());
        Collections.reverse(taskList);

        tasksAdapter.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        updateProgress();
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getAllTasks();
        Log.d("TASKS", taskList.toArray().toString());
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();

        updateProgress();
    }

    private void updateProgress(){
        //  Set Progress Bar Value
        int totalTasks = taskList.size();
        int completed = 0;
        for(ToDoModel task : taskList){
            if(task.getStatus() == 1){
                completed++;
            }
        }

        progressBar = findViewById(R.id.progressBar);
        progressPercent = findViewById(R.id.progressPercentage);
        progressFraction = findViewById(R.id.progressFraction);

        if(totalTasks > 0) {
            Log.d("Comp", String.valueOf(completed));

            progressBar.setProgressCompat(completed * 100 / totalTasks, true);
            progressPercent.setText(completed * 100 / totalTasks + "%");
            progressFraction.setText(completed + "/" + totalTasks);
        } else {
            progressBar.setProgressCompat(0, true);
            progressPercent.setText("No Tasks! Start by adding one.");
            progressFraction.setText("");
        }

        if(completed==totalTasks && totalTasks != 0){
            progressPercent.setTextColor(ContextCompat.getColor(this, R.color.secondary));
            progressFraction.setTextColor(ContextCompat.getColor(this, R.color.secondary));
            progressBar.setIndicatorColor(ContextCompat.getColor(this, R.color.secondary));

        } else {
            progressPercent.setTextColor(ContextCompat.getColor(this, R.color.black));
            progressFraction.setTextColor(ContextCompat.getColor(this, R.color.black));
            progressBar.setIndicatorColor(ContextCompat.getColor(this, R.color.primary));
        }
    }
}