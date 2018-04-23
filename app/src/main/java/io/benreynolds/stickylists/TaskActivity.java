package io.benreynolds.stickylists;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * {@code TaskActivity} is used to display individual '{@code TaskGroup}'s and is started when a user selects a {@code TaskGroup} from {@code TaskGroupsActivity}.
 */
public class TaskActivity extends AppCompatActivity {

    /** Used to store and access the user's '{@code TaskGroup}'s.  */
    private final ArrayList<TaskGroup> mTaskGroups = new ArrayList<>();

    /** Points to the {@code TaskGroup} that was selected from {@code TaskGroupsActivity}. */
    private TaskGroup mActiveTaskGroup;

    /** Used to format the '{@code Task}'s that belong to the selected {@code TaskGroup}. */
    private TaskListAdapter mTaskListAdapter;

    /** Used to display the '{@code Task}'s that belong to the selected {@code TaskGroup}. */
    private ListView lvListItems;

    /** Displays the name of the currently selected {@code TaskGroup}. */
    private TextView tvTaskGroupName;

    /** Displays the date that the currently selected {@code TaskGroup} was created. */
    private TextView tvTaskGroupDateCreated;

    /** Displays the amount of tasks there are within the currently selected {@code TaskGroup} and how many of them have been completed */
    private TextView tvTaskGroupTasksCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        tvTaskGroupName = findViewById(R.id.tvTaskGroupName);
        tvTaskGroupDateCreated = findViewById(R.id.tvTaskGroupDateCreated);
        tvTaskGroupTasksCompleted = findViewById(R.id.tvTaskGroupTasksCompleted);

        lvListItems = findViewById(R.id.lvListItems);
        registerForContextMenu(lvListItems);

        lvListItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toggle the completion state of the selected task
                Task selectedTask = mActiveTaskGroup.getTasks().get(position);
                selectedTask.setCompleted(!selectedTask.isCompleted());

                // Refresh the contents of the Task list.
                mTaskListAdapter.notifyDataSetChanged();

                // Update the tasks/tasks completed text to reflect the change.
                tvTaskGroupTasksCompleted.setText(String.format(getString(R.string.task_group_row_items_completed), mActiveTaskGroup.getCompletedTasks(), mActiveTaskGroup.getTasks().size()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load saved 'TaskGroup's from the device's internal storage.
        ArrayList<TaskGroup> savedTaskGroups = IOUtils.loadTaskGroups(this);

        // Re-populate the 'TaskGroup's list.
        mTaskGroups.clear();
        if(savedTaskGroups != null) {
            mTaskGroups.addAll(savedTaskGroups);
            // If for some reason no 'TaskGroup's were found, return to the TaskGroupsActivity.
            if(mTaskGroups.isEmpty()) {
                onBackPressed();
                return;
            }
        }

        // Assign the active task group using the intent passed to us from TaskGroupActivity.
        mActiveTaskGroup = mTaskGroups.get(getIntent().getIntExtra(TaskGroupsActivity.EXTRA_SELECTED_TASK_INDEX, 0));

        // Set up the ListView/ListAdapter used to display the 'Task's that belong to the selected TaskGroup.
        mTaskListAdapter = new TaskListAdapter(this, mActiveTaskGroup.getTasks());
        lvListItems.setAdapter(mTaskListAdapter);
        mTaskListAdapter.notifyDataSetChanged();

        // Set the background colour of the activity to the 'TaskGroup's colour.
        findViewById(R.id.clRoot).setBackgroundColor(mActiveTaskGroup.getColor());

        // Set the title of the ActionBar to the 'TaskGroup's name.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar().setTitle(mActiveTaskGroup.getName());
        }

        // Set the heading text information the 'TaskGroup's name, the date/time it was created and how many tasks belong to it / are completed.
        tvTaskGroupName.setText(mActiveTaskGroup.getName());
        tvTaskGroupDateCreated.setText(DateFormat.format("dd/MM/yyyy", mActiveTaskGroup.getDateCreated()).toString());
        tvTaskGroupTasksCompleted.setText(String.format(getString(R.string.task_group_row_items_completed), mActiveTaskGroup.getCompletedTasks(), mActiveTaskGroup.getTasks().size()));
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save the current state of the user's 'TaskGroup's to the device's internal storage.
        IOUtils.saveTaskGroups(this, mTaskGroups);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_add_item:
                promptUserToAddNewTask();
                return true;
            case R.id.action_delete_all:
                promptUserToDeleteAllTasks();
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        if (view.getId() == R.id.lvListItems) {
            getMenuInflater().inflate(R.menu.menu_task_long_press, contextMenu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Task selectedTask = (Task)lvListItems.getItemAtPosition(info.position);

        switch(item.getItemId()) {
            case R.id.move_up:
                ListViewUtils.moveListObjectUp(selectedTask, mActiveTaskGroup.getTasks(), lvListItems, mTaskListAdapter);
                return true;
            case R.id.move_down:
                ListViewUtils.moveListObjectDown(selectedTask, mActiveTaskGroup.getTasks(), lvListItems, mTaskListAdapter);
                return true;
            case R.id.send_top:
                ListViewUtils.sendListObjectToTop(selectedTask, mActiveTaskGroup.getTasks(), lvListItems, mTaskListAdapter);
                return true;
            case R.id.send_bottom:
                ListViewUtils.sendListObjectToBottom(selectedTask, mActiveTaskGroup.getTasks(), lvListItems, mTaskListAdapter);
                return true;
            case R.id.rename:
                promptUserToRenameListItem(selectedTask);
                return true;
            case R.id.duplicate:
                mActiveTaskGroup.addTask(new Task(selectedTask));
                mTaskListAdapter.notifyDataSetChanged();
                return true;
            case R.id.delete:
                ListViewUtils.deleteListObject(selectedTask, mActiveTaskGroup.getTasks(), lvListItems, mTaskListAdapter);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Renames the specified {@code Task}.
     * @param task {@code Task} to rename.
     * @param newDescription new description.                           
     */
    private void renameListItem(Task task, final String newDescription) {
        if(newDescription.length() >= Task.DESCRIPTION_LENGTH_MIN_CHARACTERS && newDescription.length() <= Task.DESCRIPTION_LENGTH_MAX_CHARACTERS) {
            task.setDescription(newDescription);
            mTaskListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Prompts the user with a {@code DialogBox} to confirm that they would like to delete all '{@code Task}'s.
     */
    private void promptUserToDeleteAllTasks() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_delete_tasks);

        // Add a positive button to the DialogBox that if clicked deletes all 'Task's belonging to the selected TaskGroup.
        alertDialogBuilder.setPositiveButton(R.string.dialog_delete_tasks_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListViewUtils.deleteListObjects(mActiveTaskGroup.getTasks(), lvListItems, mTaskListAdapter);
                tvTaskGroupTasksCompleted.setText(String.format(getString(R.string.task_group_row_items_completed), mActiveTaskGroup.getCompletedTasks(), mActiveTaskGroup.getTasks().size()));
            }
        });

        // Add a negative button to the DialogBox that closes the DialogBox and does not deete any 'Task's.
        alertDialogBuilder.setNegativeButton(R.string.dialog_delete_tasks_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create and show the DialogBox.
        alertDialogBuilder.create().show();
    }

    /**
     * Prompts the user with a {@code DialogBox} to rename the selected {@code Task}.
     * @param task {@code Task} to rename.
     */
    private void promptUserToRenameListItem(final Task task) {
        Pair<AlertDialog.Builder, EditText> singleLineAlertDialogPair = AlertDialogUtils.getSingleLineInputDialog(this, Task.DESCRIPTION_LENGTH_MAX_CHARACTERS);
        if(singleLineAlertDialogPair.first == null || singleLineAlertDialogPair.second == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = singleLineAlertDialogPair.first;
        final EditText etItemDescription = singleLineAlertDialogPair.second;

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_title_task_rename);
        etItemDescription.setText(task.getDescription());
        etItemDescription.setSelection(etItemDescription.getText().length());

        // Add a positive button to the DialogBox that if clicked changes the description of the Task.
        alertDialogBuilder.setPositiveButton(R.string.dialog_rename_task_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                renameListItem(task, etItemDescription.getText().toString());
            }
        });

        // Add a negative button to the DialogBox that closes the DialogBox and does change the description of the Task.
        alertDialogBuilder.setNegativeButton(R.string.dialog_rename_task_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create and show the DialogBox.
        alertDialogBuilder.create().show();
    }
    
    /**
     * Prompts the user with a {@code DialogBox} to input an item description and creates a new {@code Task}.
     */
    private void promptUserToAddNewTask() {
        Pair<AlertDialog.Builder, EditText> singleLineAlertDialogPair = AlertDialogUtils.getSingleLineInputDialog(this, Task.DESCRIPTION_LENGTH_MAX_CHARACTERS);
        if(singleLineAlertDialogPair.first == null || singleLineAlertDialogPair.second == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = singleLineAlertDialogPair.first;
        final EditText etItemDescription = singleLineAlertDialogPair.second;

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_title_task_description);

        // Add a positive button to the DialogBox that if clicked will add a new Task with the specified description.
        alertDialogBuilder.setPositiveButton(R.string.dialog_add_task_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActiveTaskGroup.addTask(new Task(etItemDescription.getText().toString().trim()));
                tvTaskGroupTasksCompleted.setText(String.format(getString(R.string.task_group_row_items_completed), mActiveTaskGroup.getCompletedTasks(), mActiveTaskGroup.getTasks().size()));
                mTaskListAdapter.notifyDataSetChanged();
            }
        });

        // Add a negative button to the DialogBox that closes the DialogBox and does not add any new 'Task's.
        alertDialogBuilder.setNegativeButton(R.string.dialog_add_task_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create and show the DialogBox.
        alertDialogBuilder.create().show();
    }

}
