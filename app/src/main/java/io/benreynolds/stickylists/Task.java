package io.benreynolds.stickylists;

import java.io.Serializable;

/**
 * {@code Task} represents a single task/item.
 */
public class Task implements Serializable  {

    /** Minimum amount of characters required for a '{@code Task}'s description. */
    public static final int DESCRIPTION_LENGTH_MIN_CHARACTERS = 1;

    /** Maximum amount of characters allowed for a '{@code Task}'s description */
    public static final int DESCRIPTION_LENGTH_MAX_CHARACTERS = 256;

    /** Default description value for '{@code Task}'s. */
    private static final String DESCRIPTION_DEFAULT = "Unnamed List Item";

    /** Description of the {@code Task}, for example: "Make dinner". */
    private String mDescription = DESCRIPTION_DEFAULT;

    /** Completion state of the {@code Task} */
    private boolean mCompleted;

    /**
     * Initializes the {@code Task} with its default values.
     */
    Task() {

    }

    Task(final Task copy) {
        setDescription(copy.getDescription());
        setCompleted(copy.isCompleted());
    }

    /**
     * Initializes the {@code Task} with the specified description.
     * @param description {@code Task}'s name.
     */
    Task(final String description) {
        setDescription(description);
    }

    /**
     * Set's the description of the {@code Task}}.
     * @param description Description of the {@code Task}, for example: "Make dinner".
     */
    public void setDescription(final String description) {
        if(description.length() >= DESCRIPTION_LENGTH_MIN_CHARACTERS && description.length() <= DESCRIPTION_LENGTH_MAX_CHARACTERS) {
            mDescription = description;
        }
    }

    /**
     * Returns the description of the {@code Task}.
     * @return Description of the {@code Task}.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Set's the completion state of the {@code Task}.
     * @param completed completion state.
     */
    public void setCompleted(final boolean completed) {
        mCompleted = completed;
    }

    /**
     * Returns the completion state of the {@code Task}.
     * @return Completion state of the {@code Task}.
     */
    public boolean isCompleted() {
        return mCompleted;
    }

}
