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

public class TaskGroupListAdapter extends ArrayAdapter<TaskGroup> {

    private final Context mContext;

    TaskGroupListAdapter(Context context, ArrayList<TaskGroup> listEntries) {
        super(context, 0, listEntries);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final TaskGroup taskGroup = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_task_group, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvDateCreated = convertView.findViewById(R.id.tvDateCreated);
        TextView tvItemsCompleted = convertView.findViewById(R.id.tvItemsCompleted);

        if(taskGroup != null) {
            tvName.setText(taskGroup.getName());
            tvDateCreated.setText(DateFormat.format("dd/MM/yyyy", taskGroup.getDateCreated()).toString());
            tvItemsCompleted.setText(String.format(mContext.getString(R.string.task_group_row_items_completed), taskGroup.getListItemsCompleted(), taskGroup.getListItems().size()));
        }

        return convertView;
    }

}
