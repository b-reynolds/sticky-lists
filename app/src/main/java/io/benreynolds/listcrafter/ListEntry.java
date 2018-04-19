package io.benreynolds.listcrafter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * {@code ListEntry} represents a collection of '{@code ListItem}'s.
 */
public class ListEntry implements Serializable {

    /** Minimum amount of characters required for a '{@code ListEntry}'s name */
    public static final int NAME_LENGTH_MIN_CHARACTERS = 1;

    /** Maximum amount of characters allowed for a '{@code ListEntry}'s name */
    public static final int NAME_LENGTH_MAX_CHARACTERS = 256;

    /** Default name value for '{@code ListEntry}'s. */
    private static final String NAME_DEFAULT = "Unnamed List";

    /** Name of the {@code ListEntry}. */
    private String mName = NAME_DEFAULT;

    /** Date/time that the {@code ListEntry} was created. */
    private Date mDateCreated = Calendar.getInstance().getTime();

    /** Collection of '{@code ListItem}'s that belong to the {@code ListEntry}. */
    private List<ListItem> mListItems = new ArrayList<>();

    /**
     * Initializes the {@code ListEntry} with its default values.
     */
    ListEntry() {

    }

    /**
     * Initializes the {@code ListEntry} with the specified name.
     * @param name {@code ListEntry}'s name.
     */
    ListEntry(final String name) {
        setName(name);
    }

    /**
     * Sets the name of the {@code ListEntry}.
     * @param name {@code ListEntry}'s name.'
     */
    public void setName(final String name) {
        if(name.length() >= NAME_LENGTH_MIN_CHARACTERS && name.length() <= NAME_LENGTH_MAX_CHARACTERS) {
            mName = name;
        }
    }

    /**
     * Returns the name of the {@code ListEntry}
     * @return Name of the {@code ListEntry}
     */
    public String getName() {
        return mName;
    }

    /**
     * Set's the date/time that the {@code ListEntry} was created.
     * @param dateCreated Date/time that the {@code ListEntry} was created.
     */
    public void setDateCreated(final Date dateCreated) {
        mDateCreated = dateCreated;
    }

    /**
     * Returns the date/time that the {@code ListEntry} was created.
     * @return Date/time that the {@code ListEntry} was created.
     */
    public Date getDateCreated() {
        return mDateCreated;
    }

    /**
     * Adds a {@code ListItem} to the {@code ListEntry}.
     * @param listItem {@code ListItem} to add.
     */
    public void addListItem(final ListItem listItem) {
        mListItems.add(listItem);
    }

    /**
     * Returns the '{@code ListItem}s that belong to the {@code ListEntry}.
     * @return '{@code ListItem}s that belong to the {@code ListEntry}.
     */
    public List<ListItem> getListItems() {
        return mListItems;
    }

    /**
     * Returns the number of '{@code ListItem}'s belonging to the {@code ListEntry} that are completed.
     * @return Number of '{@code ListItem}'s belonging to the {@code ListEntry} that are completed.
     */
    public int getListItemsCompleted() {
        int itemsCompleted = 0;
        for(ListItem listItem : getListItems()) {
            if(listItem.isCompleted()) {
                itemsCompleted++;
            }
        }
        return itemsCompleted;
    }

}
