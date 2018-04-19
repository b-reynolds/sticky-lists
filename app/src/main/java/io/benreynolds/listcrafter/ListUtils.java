package io.benreynolds.listcrafter;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class ListUtils {

    /**
     * Moves the specified {@code Object} up one position in the list and updates all of the respective collections.
     * @param object {@code Object} to move.
     */
    public static <T> void moveListObjectUp(final T object, final ArrayList<T> arrayList, final ListView listView, final ArrayAdapter<T> arrayAdapter) {
        int objectIndex = arrayList.indexOf(object);
        if(objectIndex != 0) {
            Collections.swap(arrayList, objectIndex, objectIndex - 1);
            listView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }
    }


    /**
     * Moves the specified {@code Object} down one position in the list and updates all of the respective collections.
     * @param object {@code Object} to move.
     */
    public static <T> void moveListObjectDown(final T object, final ArrayList<T> arrayList, final ListView listView, final ArrayAdapter<T> arrayAdapter) {
        int objectIndex = arrayList.indexOf(object);
        if(objectIndex != arrayList.size() - 1) {
            Collections.swap(arrayList, objectIndex, objectIndex + 1);
            listView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Moves the specified {@code Object} to the top of the list and updates all of the respective collections.
     * @param object {@code Object} to move.
     */
    public static <T> void sendListObjectToTop(final T object, final ArrayList<T> arrayList, final ListView listView, final ArrayAdapter<T> arrayAdapter) {
        int objectIndex = arrayList.indexOf(object);
        if(objectIndex != 0) {
            arrayList.add(0, arrayList.remove(objectIndex));
            listView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Moves the specified {@code Object} to the bottom of the list and updates all of the respective collections.
     * @param object {@code Object} to move.
     */
    public static <T> void sendListObjectToBottom(final T object, final ArrayList<T> arrayList, final ListView listView, final ArrayAdapter<T> arrayAdapter) {
        int listItemIndex = arrayList.indexOf(object);
        if(listItemIndex != arrayList.size() - 1) {
            arrayList.add(arrayList.size() - 1, arrayList.remove(listItemIndex));
            listView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Deletes specified {@code Object} and updates all of the respective collections.
     * @param object {@code Object} to delete.
     */
    public static <T> void deleteListObject(final T object, final ArrayList<T> arrayList, final ListView listView, final ArrayAdapter<T> arrayAdapter) {
        arrayList.remove(object);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * Duplicates the specified {@code Object} and updates all of the respective collections.
     * @param object {@code Object} to duplicate.
     */
    public static <T> void duplicateListObject(final T object, final ArrayList<T> arrayList, final ListView listView, final ArrayAdapter<T> arrayAdapter) {
        arrayList.add(object);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

}
