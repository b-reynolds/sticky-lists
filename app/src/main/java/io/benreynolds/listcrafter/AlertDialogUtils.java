package io.benreynolds.listcrafter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.util.Pair;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

public class AlertDialogUtils {

    public static Pair<AlertDialog.Builder, EditText> getSingleLineInputDialog(final Context context, final int maxCharacters) {
        // Create an AlertDialog.Builder that will be used to prompt users to enter a new description for the ListItem.
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // Create a frame layout with an increased left and right margin so that the EditText box will look nicer.
        FrameLayout frameLayout = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        layoutParams.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.dialog_margin);

        // Create the EditText that will be used by users to enter their desired item description,
        final EditText etInput = new EditText(context);

        // Assign the previously defined layout parameters to the EditText and apply properties that aim to restrict invalid data entry.
        etInput.setLayoutParams(layoutParams);
        etInput.setInputType(InputType.TYPE_CLASS_TEXT);
        etInput.setFilters( new InputFilter[] { new InputFilter.LengthFilter(maxCharacters) } );
        etInput.setSingleLine();

        // Add the EditText to the FrameLayout, and assign the FrameLayout to the 'DialogBox's view.
        frameLayout.addView(etInput);
        alertDialogBuilder.setView(frameLayout);

        return new Pair<>(alertDialogBuilder, etInput);
    }

}
