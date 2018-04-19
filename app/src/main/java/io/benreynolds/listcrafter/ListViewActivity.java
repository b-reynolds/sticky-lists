package io.benreynolds.listcrafter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListViewActivity extends AppCompatActivity {

    private final ArrayList<ListEntry> mListEntries = new ArrayList<>();

    private ListEntry mListEntry;

    private ListItemListAdapter mListItemListAdapter;
    private ListView lvListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        lvListItems = findViewById(R.id.lvListItems);
        registerForContextMenu(lvListItems);

        // Clear the currently stored 'ListEntry's and 'ListItem's.
        mListEntries.clear();

        // Load in the saved 'ListEntry's.
        ArrayList<ListEntry> savedListEntries = IOUtil.loadListEntries(this);
        if(savedListEntries != null) {
            mListEntries.addAll(savedListEntries);
        }

        // Set the active ListEntry.
        int activeListItemIndex = getIntent().getIntExtra(ListsOverviewActivity.EXTRA_SELECTED_LIST_INDEX, 0);
        mListEntry = mListEntries.get(activeListItemIndex);

        mListItemListAdapter = new ListItemListAdapter(this, mListEntry.getListItems());
        lvListItems.setAdapter(mListItemListAdapter);
        mListItemListAdapter.notifyDataSetChanged();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            getSupportActionBar().setTitle(mListEntry.getName());
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
                ListUtils.moveListObjectUp(selectedListItem, mListEntry.getListItems(), lvListItems, mListItemListAdapter);
                return true;
            case R.id.move_down:
                ListUtils.moveListObjectDown(selectedListItem, mListEntry.getListItems(), lvListItems, mListItemListAdapter);
                return true;
            case R.id.send_top:
                ListUtils.sendListObjectToTop(selectedListItem, mListEntry.getListItems(), lvListItems, mListItemListAdapter);
                return true;
            case R.id.send_bottom:
                ListUtils.sendListObjectToBottom(selectedListItem, mListEntry.getListItems(), lvListItems, mListItemListAdapter);
                return true;
            case R.id.rename:
                promptUserToRenameListItem(selectedListItem);
                return true;
            case R.id.delete:
                ListUtils.deleteListObject(selectedListItem, mListEntry.getListItems(), lvListItems, mListItemListAdapter);
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

    @Override
    protected void onResume() {

        super.onResume();
    }

    /**
     * Renames the specified {@code ListItem}.
     * @param listItem {@code ListItem} to rename.
     * @param newDescription new description.                           
     */
    private void renameListItem(ListItem listItem, final String newDescription) {
        if(newDescription.length() < ListItem.DESCRIPTION_LENGTH_MIN_CHARACTERS) {
            Toast.makeText(this, String.format(getString(R.string.toast_rename_too_short), ListItem.DESCRIPTION_LENGTH_MIN_CHARACTERS), Toast.LENGTH_SHORT).show();
        }
        else if(newDescription.length() > ListItem.DESCRIPTION_LENGTH_MAX_CHARACTERS) {
            Toast.makeText(this, String.format(getString(R.string.toast_rename_too_long), ListItem.DESCRIPTION_LENGTH_MIN_CHARACTERS), Toast.LENGTH_SHORT).show();
        }
        else {
            listItem.setDescription(newDescription);
            mListItemListAdapter.notifyDataSetChanged();
        }
    }


    /**
     * Prompts the user with a {@code DialogBox} to rename the specified {@code ListItem}.
     * @param listItem {@code ListItem} to rename.
     */
    private void promptUserToRenameListItem(final ListItem listItem) {
        // Create an AlertDialog.Builder that will be used to prompt users to enter a new description for the ListItem.
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Create a frame layout with an increased left and right margin so that the EditText box will look nicer.
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

        // Create the EditText that will be used by users to enter their desired item description,
        final EditText etItemName = new EditText(this);

        // Assign the previously defined layout parameters to the EditText and apply properties that aim to restrict invalid data entry.
        etItemName.setLayoutParams(layoutParams);
        etItemName.setInputType(InputType.TYPE_CLASS_TEXT);
        etItemName.setFilters( new InputFilter[] { new InputFilter.LengthFilter(ListItem.DESCRIPTION_LENGTH_MAX_CHARACTERS) } );
        etItemName.setSingleLine();

        // Add the EditText to the FrameLayout, and assign the FrameLayout to the 'DialogBox's view.
        frameLayout.addView(etItemName);
        alertDialogBuilder.setView(frameLayout);

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_title_item_rename);

        // Add a positive button to the DialogBox that changes the description of the ListItem.
        alertDialogBuilder.setPositiveButton(R.string.dialog_rename_item_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                renameListItem(listItem, etItemName.getText().toString());
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
        // Create an AlertDialog.Builder that will be used to prompt users to enter a description for the new item.
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Create a frame layout with an increased left and right margin so that the EditText box will look nicer.
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

        // Create the EditText that will be used by users to enter their desired list name,
        final EditText etItemDescription = new EditText(this);

        // Assign the previously defined layout parameters to the EditText and apply properties that aim to restrict invalid data entry.
        etItemDescription.setLayoutParams(layoutParams);
        etItemDescription.setInputType(InputType.TYPE_CLASS_TEXT);
        etItemDescription.setFilters( new InputFilter[] { new InputFilter.LengthFilter(ListItem.DESCRIPTION_LENGTH_MAX_CHARACTERS) } );
        etItemDescription.setSingleLine();

        // Add the EditText to the FrameLayout, and assign the FrameLayout to the 'DialogBox's view.
        frameLayout.addView(etItemDescription);
        alertDialogBuilder.setView(frameLayout);

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_title_item_description);

        // Add a positive button to the DialogBox that adds a new list item that with the requested description.
        alertDialogBuilder.setPositiveButton(R.string.dialog_add_item_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListEntry.addListItem(new ListItem(etItemDescription.getText().toString().trim()));
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
