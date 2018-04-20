package io.benreynolds.listcrafter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskListAdapter extends ArrayAdapter<Task> {

    TaskListAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Task task = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_task, parent, false);
        }

        final TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        final ImageView ivCheck = convertView.findViewById(R.id.ivCheck);

        if(task != null) {
            tvDescription.setText(task.getDescription());
            ivCheck.setVisibility(task.isCompleted() ? ImageView.VISIBLE : ImageView.INVISIBLE);
            setTextStrikethrough(tvDescription, task.isCompleted());
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
