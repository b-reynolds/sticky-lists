package io.benreynolds.listcrafter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
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

public class ListViewActivity extends AppCompatActivity {

    private static final String INSTANCE_STATE_LIST_ENTRIES = "LIST_ENTRIES";
    private static final String INSTANCE_STATE_ACTIVE_LIST_ENTRY = "ACTIVE_LIST_ENTRY";

    private ArrayList<ListEntry> mListEntries = new ArrayList<>();
    private ListEntry mActiveListEntry;

    private ListItemListAdapter mListItemListAdapter;
    private ListView lvListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        lvListItems = findViewById(R.id.lvListItems);
        registerForContextMenu(lvListItems);

        if(savedInstanceState == null) {
            ArrayList<ListEntry> savedListEntries = IOUtil.loadListEntries(this);
            if(savedListEntries != null) {
                mListEntries.addAll(savedListEntries);
            }

            int activeListItemIndex = getIntent().getIntExtra(ListsOverviewActivity.EXTRA_SELECTED_LIST_INDEX, 0);
            mActiveListEntry = mListEntries.get(activeListItemIndex);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle instanceState) {
        super.onSaveInstanceState(instanceState);

        instanceState.putSerializable(INSTANCE_STATE_LIST_ENTRIES, mListEntries);
        instanceState.putSerializable(INSTANCE_STATE_ACTIVE_LIST_ENTRY, mActiveListEntry);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mListEntries = (ArrayList<ListEntry>)savedInstanceState.getSerializable(INSTANCE_STATE_LIST_ENTRIES);
        mActiveListEntry = (ListEntry)savedInstanceState.getSerializable(INSTANCE_STATE_ACTIVE_LIST_ENTRY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mListItemListAdapter = new ListItemListAdapter(this, mActiveListEntry.getListItems());
        lvListItems.setAdapter(mListItemListAdapter);
        mListItemListAdapter.notifyDataSetChanged();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar().setTitle(mActiveListEntry.getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_item:
                promptUserToAddNewListItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        if (view.getId()==R.id.lvListItems) {
            getMenuInflater().inflate(R.menu.menu_list_item_long_press, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        ListItem selectedListItem = (ListItem)lvListItems.getItemAtPosition(info.position);
        switch(item.getItemId()) {
            case R.id.move_up:
                ListUtils.moveListObjectUp(selectedListItem, mActiveListEntry.getListItems(), lvListItems, mListItemListAdapter);
                return true;
            case R.id.move_down:
                ListUtils.moveListObjectDown(selectedListItem, mActiveListEntry.getListItems(), lvListItems, mListItemListAdapter);
                return true;
            case R.id.send_top:
                ListUtils.sendListObjectToTop(selectedListItem, mActiveListEntry.getListItems(), lvListItems, mListItemListAdapter);
                return true;
            case R.id.send_bottom:
                ListUtils.sendListObjectToBottom(selectedListItem, mActiveListEntry.getListItems(), lvListItems, mListItemListAdapter);
                return true;
            case R.id.rename:
                promptUserToRenameListItem(selectedListItem);
                return true;
            case R.id.delete:
                ListUtils.deleteListObject(selectedListItem, mActiveListEntry.getListItems(), lvListItems, mListItemListAdapter);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        IOUtil.saveListEntries(this, mListEntries);
        super.onPause();
    }

    /**
     * Renames the specified {@code ListItem}.
     * @param listItem {@code ListItem} to rename.
     * @param newDescription new description.                           
     */
    private void renameListItem(ListItem listItem, final String newDescription) {
        if(newDescription.length() >= ListItem.DESCRIPTION_LENGTH_MIN_CHARACTERS && newDescription.length() <= ListItem.DESCRIPTION_LENGTH_MAX_CHARACTERS) {
            listItem.setDescription(newDescription);
            mListItemListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Prompts the user with a {@code DialogBox} to rename the specified {@code ListItem}.
     * @param listItem {@code ListItem} to rename.
     */
    private void promptUserToRenameListItem(final ListItem listItem) {
        Pair<AlertDialog.Builder, EditText> singleLineAlertDialogPair = AlertDialogUtils.getSingleLineInputDialog(this, ListItem.DESCRIPTION_LENGTH_MAX_CHARACTERS);
        if(singleLineAlertDialogPair.first == null || singleLineAlertDialogPair.second == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = singleLineAlertDialogPair.first;
        final EditText etItemDescription = singleLineAlertDialogPair.second;

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_title_item_rename);
        etItemDescription.setText(listItem.getDescription());
        etItemDescription.setSelection(etItemDescription.getText().length());

        // Add a positive button to the DialogBox that changes the description of the ListItem.
        alertDialogBuilder.setPositiveButton(R.string.dialog_rename_item_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                renameListItem(listItem, etItemDescription.getText().toString());
            }
        });

        // Add a negative button to the DialogBox that closes the DialogBox and does change the description of the ListItem.
        alertDialogBuilder.setNegativeButton(R.string.dialog_rename_item_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create and show the DialogBox.
        alertDialogBuilder.create().show();
    }
    
    /**
     * Prompts the user with a {@code DialogBox} to input an item description and creates a new {@code ListItem}.
     */
    private void promptUserToAddNewListItem() {
        Pair<AlertDialog.Builder, EditText> singleLineAlertDialogPair = AlertDialogUtils.getSingleLineInputDialog(this, ListItem.DESCRIPTION_LENGTH_MAX_CHARACTERS);
        if(singleLineAlertDialogPair.first == null || singleLineAlertDialogPair.second == null) {
            return;
        }

        final AlertDialog.Builder alertDialogBuilder = singleLineAlertDialogPair.first;
        final EditText etItemDescription = singleLineAlertDialogPair.second;

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_title_item_description);

        // Add a positive button to the DialogBox that adds a new list item that with the requested description.
        alertDialogBuilder.setPositiveButton(R.string.dialog_add_item_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActiveListEntry.addListItem(new ListItem(etItemDescription.getText().toString().trim()));
                mListItemListAdapter.notifyDataSetChanged();
            }
        });

        // Add a negative button to the DialogBox that closes the DialogBox and does not add any new list items.
        alertDialogBuilder.setNegativeButton(R.string.dialog_add_item_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Create and show the DialogBox.
        alertDialogBuilder.create().show();
    }

}
