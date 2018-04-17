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

    /** Default name value for '{@code ListEntry}'s. */
    private static final String DEFAULT_NAME = ListEntry.class.getSimpleName();

    /** Default completion state for '{@code ListEntry}'s. */
    private static final boolean DEFAULT_COMPLETION_STATE = false;

    /** Name of the {@code ListEntry}. */
    private String mName = DEFAULT_NAME;

    /** Completion state of the {@code ListEntry}. */
    private boolean mComplete = DEFAULT_COMPLETION_STATE;

    /** Date/time that the {@code ListEntry} was created. */
    private Date mDateCreated = Calendar.getInstance().getTime();

    /** Collection of '{@code ListItem}'s that belong to the {@code ListEntry}. */
    private List<ListItem> mListItems = new ArrayList<>();

    /**
     * Sets the name of the {@code ListEntry}.
     * @param name '{@code ListEntry}'s name.
     */
    public void setName(final String name) {
        mName = name;
    }

    /**
     * Returns the name of the {@code ListEntry}
     * @return Name of the {@code ListEntry}
     */
    public String getName() {
        return mName;
    }

    /**
     * Set's the completion state of the {@code ListEntry}.
     * @param complete Completion state of the {@code ListEntry}.
     */
    public void setComplete(boolean complete) {
        mComplete = complete;
    }

    /**
     * Returns the completion state of the {@code ListEntry}.
     * @return Completion state of the {@code ListEntry}.
     */
    public boolean isComplete() {
        return mComplete;
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

}
