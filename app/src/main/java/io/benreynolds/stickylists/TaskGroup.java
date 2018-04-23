package io.benreynolds.stickylists;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * {@code TaskGroup} represents a collection of '{@code Task}'s.
 */
public class TaskGroup implements Serializable {

    /** Available {@code TaskGroup} colors  **/
    public static final int COLOR_YELLOW = Color.parseColor("#FFF982");
    public static final int COLOR_GREEN = Color.parseColor("#A1FFA5");
    public static final int COLOR_ORANGE = Color.parseColor("#FFCBA6");
    public static final int COLOR_BLUE = Color.parseColor("#CCF5FF");

    /** Default color assigned to new '{@code TaskGroup}'s **/
    private static final int COLOR_DEFAULT = COLOR_YELLOW;

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

    /** Task colour */
    private int mColor = COLOR_DEFAULT;

    /** Collection of '{@code Task}'s that belong to the {@code TaskGroup}. */
    private ArrayList<Task> mTasks = new ArrayList<>();

    /**
     * Initializes the {@code TaskGroup} with its default values.
     */
    TaskGroup() {

    }

    TaskGroup(TaskGroup copy) {
        setName(copy.getName());
        setDateCreated((Date)copy.getDateCreated().clone());
        setColor(copy.getColor());
        for(Task task : copy.getTasks()) {
            addTask(new Task(task));
        }
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
    private void setDateCreated(final Date dateCreated) {
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
     * Sets the '{@code TaskGroup}'s color
     * @param color Color to set.
     */
    public void setColor(final int color) {
        mColor = color;
    }

    /**
     * Returns the '{@code TaskGroup}'s color.
     * @return '{@code TaskGroup}'s color.
     */
    public int getColor() {
        return mColor;
    }

    /**
     * Returns an {@code ArrayList} containing all of the colours available to the {@code TaskGroup}.
     * @return {@code ArrayList} containing all of the colours available to the {@code TaskGroup}.
     */
    public static ArrayList<Integer> getAvailableColors() {
        ArrayList<Integer> availableColours = new ArrayList<>();
        availableColours.add(COLOR_YELLOW);
        availableColours.add(COLOR_ORANGE);
        availableColours.add(COLOR_GREEN);
        availableColours.add(COLOR_BLUE);
        return availableColours;
    }

    /**
     * Adds a {@code Task} to the {@code TaskGroup}.
     * @param task {@code Task} to add.
     */
    public void addTask(final Task task) {
        mTasks.add(task);
    }

    /**
     * Returns the '{@code Task}s that belong to the {@code TaskGroup}.
     * @return '{@code Task}s that belong to the {@code TaskGroup}.
     */
    public ArrayList<Task> getTasks() {
        return mTasks;
    }

    /**
     * Returns the number of '{@code Task}'s belonging to the {@code TaskGroup} that are completed.
     * @return Number of '{@code Task}'s belonging to the {@code TaskGroup} that are completed.
     */
    public int getCompletedTasks() {
        int itemsCompleted = 0;
        for(Task task : getTasks()) {
            if(task.isCompleted()) {
                itemsCompleted++;
            }
        }
        return itemsCompleted;
    }

}
