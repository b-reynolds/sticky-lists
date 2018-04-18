package io.benreynolds.listcrafter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ListItemListAdapter extends ArrayAdapter<ListItem> {

    ListItemListAdapter(Context context, ArrayList<ListItem> listItems) {
        super(context, 0, listItems);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ListItem listItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_list_items, parent, false);
        }

        TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        CheckBox cbCompleted = convertView.findViewById(R.id.cbCompleted);

        if(listItem != null) {
            tvDescription.setText(listItem.getDescription());
            cbCompleted.setChecked(listItem.isCompleted());
        }

        return convertView;
    }

}
