package io.benreynolds.listcrafter;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class IOUtils {

    private static final String FILE_LIST_ENTRIES = "list_entries.ser";

    public static boolean saveTaskGroups(final Context context, final ArrayList<TaskGroup> listEntries) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(FILE_LIST_ENTRIES, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(listEntries);
            objectOutputStream.close();

            fileOutputStream.close();
        }
        catch(IOException exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<TaskGroup> loadTaskGroups(final Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput(FILE_LIST_ENTRIES);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            ArrayList<TaskGroup> listEntries = (ArrayList<TaskGroup>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return listEntries;
        }
        catch(IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
