package io.benreynolds.listcrafter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

        final TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        final CheckBox cbCompleted = convertView.findViewById(R.id.cbCompleted);
        cbCompleted.setFocusable(false);

        if(listItem != null) {
            tvDescription.setText(listItem.getDescription());
            cbCompleted.setChecked(listItem.isCompleted());
            setTextStrikethrough(tvDescription, listItem.isCompleted());

            cbCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listItem.setCompleted(isChecked);
                    setTextStrikethrough(tvDescription, isChecked);
                }
            });
        }

        return convertView;
    }

    private void setTextStrikethrough(TextView textView, boolean enabled) {
        if(enabled) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            textView.setPaintFlags(textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

}
