package co.deepthought.quickscan;

import java.util.*;

/**
 * A stateful filter for querying an index
 */
public class Query {

    protected final Set<String> conjunctiveTags;
    protected final List<Set<String>> disjunctiveTags;
    protected final Map<String, Integer> fieldMaxs;
    protected final Map<String, Integer> fieldMins;
    protected final Map<String, Double> preferences;

    public Query() {
        this.fieldMaxs = new HashMap<String, Integer>();
        this.fieldMins = new HashMap<String, Integer>();
        this.conjunctiveTags = new HashSet<String>();
        this.disjunctiveTags = new ArrayList<Set<String>>();
        this.preferences = new HashMap<String, Double>();
    }

    /**
     * Add a filter on a numeric field, ensuring it is in the speicified range
     * @param field the name of the field to filter on
     * @param min the minimum value, inclusive
     * @param max the maximum value, inclusive
     */
    public void filterFieldRange(final String field, final int min, final int max) {
        this.filterFieldMax(field, max);
        this.filterFieldMin(field, min);
    }

    /**
     * Add a filter on a numeric field, ensuring it is below the max
     * @param field the name of the field to filter on
     * @param max the maximum value, inclusive
     */
    public void filterFieldMax(final String field, final int max) {
        if(this.fieldMaxs.containsKey(field)) {
            this.fieldMaxs.put(field, Math.min(max, this.fieldMaxs.get(field)));
        }
        else {
            this.fieldMaxs.put(field, max);
        }
    }

    /**
     * Add a filter on a numeric field, ensuring it is above the min
     * @param field the name of the field to filter onmax
     * @param min the minimum value, inclusive
     */
    public void filterFieldMin(final String field, final int min) {
        if(this.fieldMins.containsKey(field)) {
            this.fieldMins.put(field, Math.max(min, this.fieldMins.get(field)));
        }
        else {
            this.fieldMins.put(field, min);
        }
    }

    /**
     * Ensure the document has all provided tags
     * @param tags
     */
    public void filterTagsAll(final String... tags) {
        for(final String tag : tags) {
            this.conjunctiveTags.add(tag);
        }
    }

    /**
     * Ensure the document has at least one  the provided tags
     * @param tags
     */
    public void filterTagsAny(final String... tags) {
        final Set<String> tagSet = new HashSet<String>();
        for(final String tag : tags) {
            tagSet.add(tag);
        }
        this.disjunctiveTags.add(tagSet);
    }

    public void setPreference(final String scoreField, final double weight) {
        this.preferences.put(scoreField, weight);
    }

}
