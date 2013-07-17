package co.deepthought.quickscan.store;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import org.apache.commons.collections.ListUtils;

import java.util.*;

@Entity
public class Result extends HavingFields {

    @PrimaryKey
    private String id;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String shardId;

    public List<Document> documents;
    private String payload;

    private Result() {}

    public Result(final String id, final String shardId, final String payload) {
        this.id = id;
        this.shardId = shardId;
        this.documents = new ArrayList<>();
        this.payload = payload;
    }

    public Document createDocument(final String documentId) {
        final Document document = new Document(documentId);
        this.documents.add(document);
        return document;
    }

    public Collection<Field> getAllFields() {
        final Collection<Field> results = new ArrayList<>(this.getFields());
        for(final Document document : this.getDocuments()) {
            results.addAll(document.getFields());
        }
        return results;
    }

    public Collection<Score> getAllScores() {
        final Collection<Score> results = new ArrayList<>(this.getScores());
        for(final Document document : this.getDocuments()) {
            results.addAll(document.getScores());
        }
        return results;
    }

    public Collection<Tag> getAllTags() {
        final Collection<Tag> results = new ArrayList<>(this.getTags());
        for(final Document document : this.getDocuments()) {
            results.addAll(document.getTags());
        }
        return results;
    }

    public Document getDocumentById(final String documentId) {
        for(final Document document : this.documents) {
            if(document.getId().equals(documentId)) {
                return document;
            }
        }
        return null;
    }

    public Collection<Document> getDocuments() {
        return this.documents;
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