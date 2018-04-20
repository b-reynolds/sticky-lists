package io.benreynolds.listcrafter;

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

public class TaskActivity extends AppCompatActivity {

    private static final String INSTANCE_STATE_LIST_ENTRIES = "LIST_ENTRIES";
    private static final String INSTANCE_STATE_ACTIVE_LIST_ENTRY = "ACTIVE_LIST_ENTRY";

    private ArrayList<TaskGroup> mListEntries = new ArrayList<>();
    private TaskGroup mActiveTaskGroup;

    private TaskListAdapter mTaskListAdapter;
    private ListView lvListItems;

    private TextView tvTaskGroupName;
    private TextView tvTaskGroupDateCreated;
    private TextView tvTaskGroupTasksCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        lvListItems = findViewById(R.id.lvListItems);
        registerForContextMenu(lvListItems);

        lvListItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActiveTaskGroup.getListItems().get(position).setCompleted(!mActiveTaskGroup.getListItems().get(position).isCompleted());
                mTaskListAdapter.notifyDataSetChanged();
                tvTaskGroupTasksCompleted.setText(String.format(getString(R.string.task_group_row_items_completed), mActiveTaskGroup.getListItemsCompleted(), mActiveTaskGroup.getListItems().size()));
            }
        });

        tvTaskGroupName = findViewById(R.id.tvTaskGroupName);
        tvTaskGroupDateCreated = findViewById(R.id.tvTaskGroupDateCreated);
        tvTaskGroupTasksCompleted = findViewById(R.id.tvTaskGroupTasksCompleted);

        if(savedInstanceState == null) {
            ArrayList<TaskGroup> savedListEntries = IOUtils.loadListEntries(this);
            if(savedListEntries != null) {
                mListEntries.addAll(savedListEntries);
            }

            int activeListItemIndex = getIntent().getIntExtra(TaskGroupsActivity.EXTRA_SELECTED_LIST_INDEX, 0);
            mActiveTaskGroup = mListEntries.get(activeListItemIndex);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle instanceState) {
        super.onSaveInstanceState(instanceState);

        instanceState.putSerializable(INSTANCE_STATE_LIST_ENTRIES, mListEntries);
        instanceState.putSerializable(INSTANCE_STATE_ACTIVE_LIST_ENTRY, mActiveTaskGroup);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mListEntries = (ArrayList<TaskGroup>)savedInstanceState.getSerializable(INSTANCE_STATE_LIST_ENTRIES);
        mActiveTaskGroup = (TaskGroup)savedInstanceState.getSerializable(INSTANCE_STATE_ACTIVE_LIST_ENTRY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvTaskGroupName.setText(mActiveTaskGroup.getName());
        tvTaskGroupDateCreated.setText(DateFormat.format("dd/MM/yyyy", mActiveTaskGroup.getDateCreated()).toString());
        tvTaskGroupTasksCompleted.setText(String.format(getString(R.string.task_group_row_items_completed), mActiveTaskGroup.getListItemsCompleted(), mActiveTaskGroup.getListItems().size()));

        mTaskListAdapter = new TaskListAdapter(this, mActiveTaskGroup.getListItems());
        lvListItems.setAdapter(mTaskListAdapter);
        mTaskListAdapter.notifyDataSetChanged();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar().setTitle(mActiveTaskGroup.getName());
        }

        View constraintLayout = findViewById(R.id.clRoot);
        constraintLayout.setBackgroundColor(mActiveTaskGroup.getColor());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_item:
                promptUserToAddNewListItem();
                return true;
            case R.id.action_delete_all:
                promptUserToDeleteAll();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        if (view.getId()==R.id.lvListItems) {
            getMenuInflater().inflate(R.menu.menu_task_long_press, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Task selectedTask = (Task)lvListItems.getItemAtPosition(info.position);
        switch(item.getItemId()) {
            case R.id.move_up:
                ListViewUtils.moveListObjectUp(selectedTask, mActiveTaskGroup.getListItems(), lvListItems, mTaskListAdapter);
                return true;
            case R.id.move_down:
                ListViewUtils.moveListObjectDown(selectedTask, mActiveTaskGroup.getListItems(), lvListItems, mTaskListAdapter);
                return true;
            case R.id.send_top:
                ListViewUtils.sendListObjectToTop(selectedTask, mActiveTaskGroup.getListItems(), lvListItems, mTaskListAdapter);
                return true;
            case R.id.send_bottom:
                ListViewUtils.sendListObjectToBottom(selectedTask, mActiveTaskGroup.getListItems(), lvListItems, mTaskListAdapter);
                return true;
            case R.id.rename:
                promptUserToRenameListItem(selectedTask);
                return true;
            case R.id.duplicate:
                ListViewUtils.duplicateListObject(selectedTask, mActiveTaskGroup.getListItems(), lvListItems, mTaskListAdapter);
                return true;
            case R.id.delete:
                ListViewUtils.deleteListObject(selectedTask, mActiveTaskGroup.getListItems(), lvListItems, mTaskListAdapter);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        IOUtils.saveListEntries(this, mListEntries);
        super.onPause();
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
     * Prompts the user with a {@code DialogBox} to confirm that they would like to delete all '{@code TaskGroup}'s.
     */
    private void promptUserToDeleteAll() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_delete_tasks);

        // Add a positive button to the DialogBox that adds a new list that is named as requested.
        alertDialogBuilder.setPositiveButton(R.string.dialog_delete_tasks_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListViewUtils.deleteListObjects(mActiveTaskGroup.getListItems(), lvListItems, mTaskListAdapter);
                tvTaskGroupTasksCompleted.setText(String.format(getString(R.string.task_group_row_items_completed), mActiveTaskGroup.getListItemsCompleted(), mActiveTaskGroup.getListItems().size()));
            }
        });

        // Add a negative button to the DialogBox that closes the DialogBox and does not add any new lists.
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
     * Prompts the user with a {@code DialogBox} to rename the specified {@code Task}.
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

        // Add a positive button to the DialogBox that changes the description of the Task.
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
    private void promptUserToAddNewListItem() {
        Pair<AlertDialog.Builder, EditText> singleLineAlertDialogPair = AlertDialogUtils.getSingleLineInputDialog(this, Task.DESCRIPTION_LENGTH_MAX_CHARACTERS);
        if(singleLineAlertDialogPair.first == null || singleLineAlertDialogPair.second == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = singleLineAlertDialogPair.first;
        final EditText etItemDescription = singleLineAlertDialogPair.second;

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_title_task_description);

        // Add a positive button to the DialogBox that adds a new list item that with the requested description.
        alertDialogBuilder.setPositiveButton(R.string.dialog_add_task_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActiveTaskGroup.addListItem(new Task(etItemDescription.getText().toString().trim()));
                tvTaskGroupTasksCompleted.setText(String.format(getString(R.string.task_group_row_items_completed), mActiveTaskGroup.getListItemsCompleted(), mActiveTaskGroup.getListItems().size()));
                mTaskListAdapter.notifyDataSetChanged();
            }
        });

        // Add a negative button to the DialogBox that closes the DialogBox and does not add any new list items.
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
