package io.benreynolds.listcrafter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

}
