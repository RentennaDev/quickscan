package co.deepthought.quickscan.store;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import org.apache.commons.collections.ListUtils;

import java.util.*;

@Entity
public class Document {

    @PrimaryKey
    private String id;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String shardId;

    private boolean deprecated;
    private List<Field> fields;
    private List<Score> scores;
    private List<Tag> tags;
    private String payload;

    private Document() {}

    public Document(final String id, final String shardId, final String payload) {
        this.id = id;
        this.shardId = shardId;
        this.fields = new ArrayList<>();
        this.scores = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.payload = payload;
        this.deprecated = false;
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

    public void deprecate() {
        this.deprecated = true;
    }

    public List<Field> getFields() {
        return this.fields;
    }

    public boolean getDeprecated() {
        return this.deprecated;
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

    public String getId() {
        return this.id;
    }

    public String getPayload() {
        return this.payload;
    }

    public String getShardId() {
        return this.shardId;
    }

}