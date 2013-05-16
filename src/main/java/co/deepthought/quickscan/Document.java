package co.deepthought.quickscan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A document describes the raw fields that make up a particular document.
 */
public class Document {

    protected final Map<String, Integer> fields;
    protected final String identifier;
    protected final Map<String, Double> scores;
    protected final Map<String, Double> scoreConfidences;
    protected final Set<String> tags;

    public Document(final String identifier) {
        this.fields = new HashMap<String, Integer>();
        this.identifier = identifier;
        this.scores = new HashMap<String, Double>();
        this.scoreConfidences = new HashMap<String, Double>();
        this.tags = new HashSet<String>();
    }

    /**
     * Add one or more tags for filtering.
     * @param tags
     */
    public void addTag(final String... tags) {
        for(final String tag : tags) {
            this.tags.add(tag);
        }
    }

    /**
     * Add a field with a particular value.
     * @param fieldName
     * @param value
     */
    public void addField(final String fieldName, final int value) {
        this.fields.put(fieldName, value);
    }

    /**
     * Add a score with a particular value and confidence.
     * @param scoreName
     * @param score - should be in the range [0,1] where 1 is a better score
     * @param confidence - should be in the range [0,1] where 1 is most confident
     */
    public void addScore(final String scoreName, final double score, final double confidence) {
        this.scores.put(scoreName, score);
        this.scoreConfidences.put(scoreName, confidence);
    }

}