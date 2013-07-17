package co.deepthought.quickscan.store;

import com.sleepycat.persist.model.Persistent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Persistent
public abstract class HavingFields {

    private List<Field> fields;
    private List<Score> scores;
    private List<Tag> tags;

    protected HavingFields() {
        this.fields = new ArrayList<>();
        this.scores = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public void addField(final String fieldName, final double value) {
        final Field field = new Field(fieldName, value);
        this.fields.add(field);
    }

    public void addTag(final String tagName) {
        final Tag tag = new Tag(tagName);
        this.tags.add(tag);
    }

    public void addScore(final String scoreName, final boolean phantom, final double value) {
        final Score score = new Score(scoreName, phantom, value);
        this.scores.add(score);
    }

    public List<Field> getFields() {
        return this.fields;
    }

    public Double getFieldValue(final String fieldName) {
        for(final Field field : this.fields) {
            if(field.getName().equals(fieldName)) {
                return field.getValue();
            }
        }
        return null;
    }

    public Map<String, Double> getFieldValues() {
        final Map<String, Double> fieldValues = new HashMap<>();
        for(final Field field : this.fields) {
            fieldValues.put(field.getName(), field.getValue());
        }
        return fieldValues;
    }

    public List<Score> getScores() {
        return this.scores;
    }

    public Double getScoreValue(final String scoreName) {
        for(final Score score : this.scores) {
            if(score.getName().equals(scoreName)) {
                return score.getValue();
            }
        }
        return null;
    }

    public Map<String, Double> getScoreValues() {
        final Map<String, Double> scoreValues = new HashMap<>();
        for(final Score score : this.scores) {
            scoreValues.put(score.getName(), score.getValue());
        }
        return scoreValues;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public List<String> getTagNames() {
        final List<String> tagNames = new ArrayList<>();
        for(final Tag tag : this.tags) {
            tagNames.add(tag.getName());
        }
        return tagNames;
    }

    public boolean hasTag(final String tagName) {
        for(final Tag tag : this.tags) {
            if(tag.getName().equals(tagName)) {
                return true;
            }
        }
        return false;
    }

}
