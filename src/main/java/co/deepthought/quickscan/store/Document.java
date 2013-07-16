package co.deepthought.quickscan.store;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.*;

/**
 * The ORM class for documents being stored in the sqlite store.
 */
@DatabaseTable()
public class Document {

    public static class Dao extends BaseDaoImpl<Document, Integer> {
        public Dao(ConnectionSource connectionSource) throws SQLException {
            super(connectionSource, Document.class);
        }
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, index = true)
    private Date createDate;

    @DatabaseField(canBeNull = false, uniqueIndex = true)
    private String documentId;

    @DatabaseField(canBeNull = false, index = true)
    private String resultId;

    @DatabaseField(canBeNull = false, index = true)
    private String shardId;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Field> fields;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Score> scores;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Tag> tags;

    private Document() {

    }

    public Document(final String documentId, final String resultId, final String shardId) {
        this.createDate = new Date();
        this.documentId = documentId;
        this.resultId = resultId;
        this.shardId = shardId;
    }

    public void addField(final String fieldName, final double value) throws SQLException {
        final Field field = new Field(fieldName, value);
        this.fields.add(field);
    }

    public void addTag(final String tagName) throws SQLException {
        final Tag tag = new Tag(tagName);
        this.tags.add(tag);
    }

    public void addScore(final String scoreName, final boolean phantom, final double value) throws SQLException {
        final Score score = new Score(scoreName, phantom, value);
        this.scores.add(score);
    }

    public String getDocumentId() {
        return documentId;
    }

    public Collection<Field> getFields() {
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
        final Map<String, Double> fieldValues = new HashMap<String, Double>();
        for(final Field field : this.fields) {
            fieldValues.put(field.getName(), field.getValue());
        }
        return fieldValues;
    }

    public String getResultId() {
        return resultId;
    }

    public String getShardId() {
        return shardId;
    }

    public Collection<Score> getScores() {
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
        final Map<String, Double> scoreValues = new HashMap<String, Double>();
        for(final Score score : this.scores) {
            scoreValues.put(score.getName(), score.getValue());
        }
        return scoreValues;
    }

    public Collection<Tag> getTags() {
        return this.tags;
    }

    public List<String> getTagNames() {
        final List<String> tagNames = new ArrayList<String>();
        for(final Tag tag : this.tags) {
            tagNames.add(tag.getName());
        }
        return tagNames;
    }

    public boolean isPersisted() {
        return this.id > 0;
    }

    public boolean hasTag(final String tagName) {
        for(final Tag tag : this.tags) {
            if(tag.getName().equals(tagName)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "Document(" + this.documentId + ")";
    }

}