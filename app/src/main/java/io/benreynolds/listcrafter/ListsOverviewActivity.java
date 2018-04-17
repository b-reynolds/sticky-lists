package io.benreynolds.listcrafter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class ListsOverviewActivity extends AppCompatActivity {

    final ArrayList<ListEntry> mListEntries = new ArrayList<>();
    private ListEntryListAdapter mListEntryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists_overview);

        mListEntryListAdapter = new ListEntryListAdapter(this, mListEntries);
        ListView lvListEntries = findViewById(R.id.lvListEntries);
        lvListEntries.setAdapter(mListEntryListAdapter);

        ArrayList<ListEntry> savedLists = IOUtil.loadListEntries(this);
        if(savedLists != null && !savedLists.isEmpty()) {
            mListEntries.addAll(savedLists);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lists_overview, menu);
        return true;
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

    private void promptUserToAddNewListEntry() {
        // Create an AlertDialog.Builder that will be used to prompt users to enter a name for the new list.
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Create a frame layout with an increased left and right margin so that the EditText box will look nicer.
        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

        // Create the EditText that will be used by users to enter their desired list name,
        final EditText etListName = new EditText(this);

        // Assign the previously defined layout parameters to the EditText and apply properties that aim to restrict invalid data entry.
        etListName.setLayoutParams(layoutParams);
        etListName.setInputType(InputType.TYPE_CLASS_TEXT);
        etListName.setFilters( new InputFilter[] { new InputFilter.LengthFilter(ListEntry.NAME_LENGTH_MAX_CHARACTERS) } );
        etListName.setSingleLine();

        // Add the EditText to the FrameLayout, and assign the FrameLayout to the 'DialogBox's view.
        frameLayout.addView(etListName);
        alertDialogBuilder.setView(frameLayout);

        // Set the title text of the DialogBox.
        alertDialogBuilder.setTitle(R.string.dialog_title_list_name);

        // Add a positive button to the DialogBox that adds a new list that is named as requested.
        alertDialogBuilder.setPositiveButton(R.string.dialog_add_list_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListEntries.add(new ListEntry(etListName.getText().toString()));
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

}
