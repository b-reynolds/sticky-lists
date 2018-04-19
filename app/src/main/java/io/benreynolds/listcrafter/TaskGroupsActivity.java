package io.benreynolds.listcrafter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * {@code TaskGroupsActivity} is the root activity of the application, it allows users to create and manage '{@code TaskGroup}'s.
 */
public class TaskGroupsActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_LIST_INDEX = "SELECTED_LIST_INDEX";

    final ArrayList<TaskGroup> mListEntries = new ArrayList<>();
    private TaskGroupListAdapter mTaskGroupListAdapter;
    private ListView lvListEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_groups);

        mTaskGroupListAdapter = new TaskGroupListAdapter(this, mListEntries);
        lvListEntries = findViewById(R.id.lvListEntries);
        registerForContextMenu(lvListEntries);

        lvListEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent startListViewActivity = new Intent(view.getContext(), TaskActivity.class);
                startListViewActivity.putExtra(EXTRA_SELECTED_LIST_INDEX, position);
                startActivity(startListViewActivity);
            }
        });
    }


    @Override
    public void onResume() {

        mListEntries.clear();

        ArrayList<TaskGroup> savedLists = IOUtils.loadListEntries(this);
        if(savedLists != null) {
            mListEntries.addAll(savedLists);
        }

        lvListEntries.setAdapter(mTaskGroupListAdapter);
        mTaskGroupListAdapter.notifyDataSetChanged();


        super.onResume();
    }

    @Override
    protected void onPause() {
        IOUtils.saveListEntries(this, mListEntries);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_groups_activity, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        if (view.getId()==R.id.lvListEntries) {
            getMenuInflater().inflate(R.menu.menu_task_group_long_press, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        TaskGroup selectedTaskGroup = (TaskGroup)lvListEntries.getItemAtPosition(info.position);
        switch(item.getItemId()) {
            case R.id.move_up:
                ListViewUtils.moveListObjectUp(selectedTaskGroup, mListEntries, lvListEntries, mTaskGroupListAdapter);
                return true;
            case R.id.move_down:
                ListViewUtils.moveListObjectDown(selectedTaskGroup, mListEntries, lvListEntries, mTaskGroupListAdapter);
                return true;
            case R.id.send_top:
                ListViewUtils.sendListObjectToTop(selectedTaskGroup, mListEntries, lvListEntries, mTaskGroupListAdapter);
                return true;
            case R.id.send_bottom:
                ListViewUtils.sendListObjectToBottom(selectedTaskGroup, mListEntries, lvListEntries, mTaskGroupListAdapter);
                return true;
            case R.id.rename:
                promptUserToRenameListEntry(selectedTaskGroup);
                return true;
            case R.id.duplicate:
                ListViewUtils.duplicateListObject(selectedTaskGroup, mListEntries, lvListEntries, mTaskGroupListAdapter);
                return true;
            case R.id.delete:
                ListViewUtils.deleteListObject(selectedTaskGroup, mListEntries, lvListEntries, mTaskGroupListAdapter);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_list:
                promptUserToAddNewListEntry();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Renames the specified {@code TaskGroup}.
     * @param taskGroup {@code TaskGroup} to rename.
     */
    private void renameListEntry(TaskGroup taskGroup, final String newName) {
        if(newName.length() >= TaskGroup.NAME_LENGTH_MIN_CHARACTERS && newName.length() <= TaskGroup.NAME_LENGTH_MAX_CHARACTERS) {
            taskGroup.setName(newName);
            mTaskGroupListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Prompts the user with a {@code DialogBox} to input a list name and creates a new {@code TaskGroup}.
     */
    private void promptUserToAddNewListEntry() {
        Pair<AlertDialog.Builder, EditText> singleLineAlertDialogPair = AlertDialogUtils.getSingleLineInputDialog(this, Task.DESCRIPTION_LENGTH_MAX_CHARACTERS);
        if(singleLineAlertDialogPair.first == null || singleLineAlertDialogPair.second == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = singleLineAlertDialogPair.first;
        final EditText etItemName = singleLineAlertDialogPair.second;

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_task_group_name);

        // Add a positive button to the DialogBox that adds a new list that is named as requested.
        alertDialogBuilder.setPositiveButton(R.string.dialog_add_task_group_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListEntries.add(new TaskGroup(etItemName.getText().toString().trim()));
                mTaskGroupListAdapter.notifyDataSetChanged();
                IOUtils.saveListEntries(TaskGroupsActivity.this, mListEntries);
            }
        });

        // Add a negative button to the DialogBox that closes the DialogBox and does not add any new lists.
        alertDialogBuilder.setNegativeButton(R.string.dialog_add_task_group_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create and show the DialogBox.
        alertDialogBuilder.create().show();
    }

    /**
     * Prompts the user with a {@code DialogBox} to rename the specified {@code TaskGroup}.
     * @param taskGroup {@code TaskGroup} to rename.
     */
    private void promptUserToRenameListEntry(final TaskGroup taskGroup) {
        Pair<AlertDialog.Builder, EditText> singleLineAlertDialogPair = AlertDialogUtils.getSingleLineInputDialog(this, Task.DESCRIPTION_LENGTH_MAX_CHARACTERS);
        if(singleLineAlertDialogPair.first == null || singleLineAlertDialogPair.second == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = singleLineAlertDialogPair.first;
        final EditText etItemName = singleLineAlertDialogPair.second;

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_task_group_rename);
        etItemName.setText(taskGroup.getName());
        etItemName.setSelection(etItemName.getText().length());

        // Add a positive button to the DialogBox that renames the TaskGroup.
        alertDialogBuilder.setPositiveButton(R.string.dialog_rename_task_group_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                renameListEntry(taskGroup, etItemName.getText().toString());
            }
        });

        // Add a negative button to the DialogBox that closes the DialogBox and does not rename the TaskGroup.
        alertDialogBuilder.setNegativeButton(R.string.dialog_rename_task_group_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create and show the DialogBox.
        alertDialogBuilder.create().show();
    }

}
