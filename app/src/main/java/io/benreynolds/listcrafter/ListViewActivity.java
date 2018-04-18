package io.benreynolds.listcrafter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {

    private final ArrayList<ListEntry> mListEntries = new ArrayList<>();

    private ListEntry mListEntry;

    final ArrayList<ListItem> mListItems = new ArrayList<>();
    private ListItemListAdapter mListItemListAdapter;
    private ListView lvListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        mListItemListAdapter = new ListItemListAdapter(this, mListItems);
        lvListItems = findViewById(R.id.lvListItems);
        lvListItems.setAdapter(mListItemListAdapter);
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
    protected void onPause() {
        IOUtil.saveListEntries(this, mListEntries);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Clear the currently stored 'ListEntry's.
        mListEntries.clear();

        // Load in the saved 'ListEntry's.
        ArrayList<ListEntry> savedListEntries = IOUtil.loadListEntries(this);
        if(savedListEntries != null) {
            mListEntries.addAll(savedListEntries);
        }

        // Set the active ListEntry.
        int activeListItemIndex = getIntent().getIntExtra(ListsOverviewActivity.EXTRA_SELECTED_LIST_INDEX, 0);
        mListEntry = mListEntries.get(activeListItemIndex);

        // Clear and repopulate the active 'ListEntry's 'ListItem's.
        mListItems.clear();
        mListItems.addAll(mListEntry.getListItems());
        mListItemListAdapter.notifyDataSetChanged();

        super.onResume();
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
                mListEntry.addListItem(new ListItem(etItemDescription.getText().toString()));
                mListItems.add(mListEntry.getListItems().get(mListEntry.getListItems().size() - 1));
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
