package io.benreynolds.listcrafter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * {@code TaskGroup} represents a collection of '{@code Task}'s.
 */
public class TaskGroup implements Serializable {

    /** Minimum amount of characters required for a '{@code TaskGroup}'s name */
    public static final int NAME_LENGTH_MIN_CHARACTERS = 1;

    /** Maximum amount of characters allowed for a '{@code TaskGroup}'s name */
    public static final int NAME_LENGTH_MAX_CHARACTERS = 256;

    /** Default name value for '{@code TaskGroup}'s. */
    private static final String NAME_DEFAULT = "Unnamed List";

    /** Name of the {@code TaskGroup}. */
    private String mName = NAME_DEFAULT;

    /** Date/time that the {@code TaskGroup} was created. */
    private Date mDateCreated = Calendar.getInstance().getTime();

    /** Collection of '{@code Task}'s that belong to the {@code TaskGroup}. */
    private ArrayList<Task> mTasks = new ArrayList<>();

    /**
     * Initializes the {@code TaskGroup} with its default values.
     */
    TaskGroup() {

    }

    /**
     * Initializes the {@code TaskGroup} with the specified name.
     * @param name {@code TaskGroup}'s name.
     */
    TaskGroup(final String name) {
        setName(name);
    }

    /**
     * Sets the name of the {@code TaskGroup}.
     * @param name {@code TaskGroup}'s name.'
     */
    public void setName(final String name) {
        if(name.length() >= NAME_LENGTH_MIN_CHARACTERS && name.length() <= NAME_LENGTH_MAX_CHARACTERS) {
            mName = name;
        }
    }

    /**
     * Returns the name of the {@code TaskGroup}
     * @return Name of the {@code TaskGroup}
     */
    public String getName() {
        return mName;
    }

    /**
     * Set's the date/time that the {@code TaskGroup} was created.
     * @param dateCreated Date/time that the {@code TaskGroup} was created.
     */
    public void setDateCreated(final Date dateCreated) {
        mDateCreated = dateCreated;
    }

    /**
     * Returns the date/time that the {@code TaskGroup} was created.
     * @return Date/time that the {@code TaskGroup} was created.
     */
    public Date getDateCreated() {
        return mDateCreated;
    }

    /**
     * Adds a {@code Task} to the {@code TaskGroup}.
     * @param task {@code Task} to add.
     */
    public void addListItem(final Task task) {
        mTasks.add(task);
    }

    /**
     * Returns the '{@code Task}s that belong to the {@code TaskGroup}.
     * @return '{@code Task}s that belong to the {@code TaskGroup}.
     */
    public ArrayList<Task> getListItems() {
        return mTasks;
    }

    /**
     * Returns the number of '{@code Task}'s belonging to the {@code TaskGroup} that are completed.
     * @return Number of '{@code Task}'s belonging to the {@code TaskGroup} that are completed.
     */
    public int getListItemsCompleted() {
        int itemsCompleted = 0;
        for(Task task : getListItems()) {
            if(task.isCompleted()) {
                itemsCompleted++;
            }
        }
        return itemsCompleted;
    }

}