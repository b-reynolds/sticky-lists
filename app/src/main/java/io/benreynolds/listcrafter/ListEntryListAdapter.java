package io.benreynolds.listcrafter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListEntryListAdapter extends ArrayAdapter<ListEntry> {

    private final Context mContext;

    ListEntryListAdapter(Context context, ArrayList<ListEntry> listEntries) {
        super(context, 0, listEntries);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ListEntry listEntry = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_list_entries, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvDateCreated = convertView.findViewById(R.id.tvDateCreated);
        TextView tvItemsCompleted = convertView.findViewById(R.id.tvItemsCompleted);

        if(listEntry != null) {
            tvName.setText(listEntry.getName());
            tvDateCreated.setText(DateFormat.format("dd/MM/yyyy", listEntry.getDateCreated()).toString());
            tvItemsCompleted.setText(String.format(mContext.getString(R.string.list_entry_adapter_items_completed), listEntry.getListItemsCompleted(), listEntry.getListItems().size()));
        }

        return convertView;
    }

}
