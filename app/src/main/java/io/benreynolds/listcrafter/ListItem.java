package io.benreynolds.listcrafter;

import java.io.Serializable;

/**
 * {@code ListItem} represents a single task/item.
 */
public class ListItem implements Serializable {

    /** Default description value for '{@code ListItem}'s. */
    private static final String DESCRIPTION_DEFAULT = "Unnamed List Item";

    /** Description of the {@code ListItem}, for example: "Make dinner". */
    private String mDescription;

    /** Completion state of the {@code ListItem} */
    private boolean mCompleted;

    /**
     * Initializes the {@code ListItem} with its default values.
     */
    ListItem() {

    }

    /**
     * Initializes the {@code ListItem} with the specified description.
     * @param description {@code ListItem}'s name.
     */
    ListItem(final String description) {
        setDescription(description);
    }

    /**
     * Set's the description of the {@code ListItem}}.
     * @param description Description of the {@code ListItem}, for example: "Make dinner".
     */
    public void setDescription(final String description) {
        mDescription = description;
    }

    /**
     * Returns the description of the {@code ListItem}.
     * @return Description of the {@code ListItem}.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Set's the completion state of the {@code ListItem}.
     * @param completed completion state.
     */
    public void setCompleted(final boolean completed) {
        mCompleted = completed;
    }

    /**
     * Returns the completion state of the {@code ListItem}.
     * @return Completion state of the {@code ListItem}.
     */
    public boolean isCompleted() {
        return mCompleted;
    }

}
