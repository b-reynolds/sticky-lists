package io.benreynolds.listcrafter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * {@code ListsOverviewActivity} is the root activity of the application, it allows users to create and manage '{@code ListEntry}'s.
 */
public class ListsOverviewActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_LIST_INDEX = "SELECTED_LIST_INDEX";

    final ArrayList<ListEntry> mListEntries = new ArrayList<>();
    private ListEntryListAdapter mListEntryListAdapter;
    private ListView lvListEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists_overview);

        mListEntryListAdapter = new ListEntryListAdapter(this, mListEntries);
        lvListEntries = findViewById(R.id.lvListEntries);
        registerForContextMenu(lvListEntries);

        lvListEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent startListViewActivity = new Intent(view.getContext(), ListViewActivity.class);
                startListViewActivity.putExtra(EXTRA_SELECTED_LIST_INDEX, position);
                startActivity(startListViewActivity);
            }
        });
    }


    @Override
    public void onResume() {

        mListEntries.clear();

        ArrayList<ListEntry> savedLists = IOUtil.loadListEntries(this);
        if(savedLists != null) {
            mListEntries.addAll(savedLists);
        }

        lvListEntries.setAdapter(mListEntryListAdapter);
        mListEntryListAdapter.notifyDataSetChanged();


        super.onResume();
    }

    @Override
    protected void onPause() {
        IOUtil.saveListEntries(this, mListEntries);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lists_overview, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        if (view.getId()==R.id.lvListEntries) {
            getMenuInflater().inflate(R.menu.menu_list_entry_long_press, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        ListEntry selectedListEntry = (ListEntry)lvListEntries.getItemAtPosition(info.position);
        switch(item.getItemId()) {
            case R.id.move_up:
                ListUtils.moveListObjectUp(selectedListEntry, mListEntries, lvListEntries, mListEntryListAdapter);
                return true;
            case R.id.move_down:
                ListUtils.moveListObjectDown(selectedListEntry, mListEntries, lvListEntries, mListEntryListAdapter);
                return true;
            case R.id.send_top:
                ListUtils.sendListObjectToTop(selectedListEntry, mListEntries, lvListEntries, mListEntryListAdapter);
                return true;
            case R.id.send_bottom:
                ListUtils.sendListObjectToBottom(selectedListEntry, mListEntries, lvListEntries, mListEntryListAdapter);
                return true;
            case R.id.rename:
                promptUserToRenameListEntry(selectedListEntry);
                return true;
            case R.id.duplicate:
                ListUtils.duplicateListObject(selectedListEntry, mListEntries, lvListEntries, mListEntryListAdapter);
                return true;
            case R.id.delete:
                ListUtils.deleteListObject(selectedListEntry, mListEntries, lvListEntries, mListEntryListAdapter);
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
     * Renames the specified {@code ListEntry}.
     * @param listEntry {@code ListEntry} to rename.
     */
    private void renameListEntry(ListEntry listEntry, final String newName) {
        if(newName.length() >= ListEntry.NAME_LENGTH_MIN_CHARACTERS && newName.length() <= ListEntry.NAME_LENGTH_MAX_CHARACTERS) {
            listEntry.setName(newName);
            mListEntryListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Prompts the user with a {@code DialogBox} to input a list name and creates a new {@code ListEntry}.
     */
    private void promptUserToAddNewListEntry() {
        Pair<AlertDialog.Builder, EditText> singleLineAlertDialogPair = AlertDialogUtils.getSingleLineInputDialog(this, ListItem.DESCRIPTION_LENGTH_MAX_CHARACTERS);
        if(singleLineAlertDialogPair.first == null || singleLineAlertDialogPair.second == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = singleLineAlertDialogPair.first;
        final EditText etItemName = singleLineAlertDialogPair.second;

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_title_list_name);

        // Add a positive button to the DialogBox that adds a new list that is named as requested.
        alertDialogBuilder.setPositiveButton(R.string.dialog_add_list_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListEntries.add(new ListEntry(etItemName.getText().toString().trim()));
                mListEntryListAdapter.notifyDataSetChanged();
                IOUtil.saveListEntries(ListsOverviewActivity.this, mListEntries);
            }
        });

        // Add a negative button to the DialogBox that closes the DialogBox and does not add any new lists.
        alertDialogBuilder.setNegativeButton(R.string.dialog_add_list_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create and show the DialogBox.
        alertDialogBuilder.create().show();
    }

    /**
     * Prompts the user with a {@code DialogBox} to rename the specified {@code ListEntry}.
     * @param listEntry {@code ListEntry} to rename.
     */
    private void promptUserToRenameListEntry(final ListEntry listEntry) {
        Pair<AlertDialog.Builder, EditText> singleLineAlertDialogPair = AlertDialogUtils.getSingleLineInputDialog(this, ListItem.DESCRIPTION_LENGTH_MAX_CHARACTERS);
        if(singleLineAlertDialogPair.first == null || singleLineAlertDialogPair.second == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = singleLineAlertDialogPair.first;
        final EditText etItemName = singleLineAlertDialogPair.second;

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_title_list_rename);
        etItemName.setText(listEntry.getName());
        etItemName.setSelection(etItemName.getText().length());

        // Add a positive button to the DialogBox that renames the ListEntry.
        alertDialogBuilder.setPositiveButton(R.string.dialog_rename_list_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                renameListEntry(listEntry, etItemName.getText().toString());
            }
        });

        // Add a negative button to the DialogBox that closes the DialogBox and does not rename the ListEntry.
        alertDialogBuilder.setNegativeButton(R.string.dialog_rename_list_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create and show the DialogBox.
        alertDialogBuilder.create().show();
    }

}
