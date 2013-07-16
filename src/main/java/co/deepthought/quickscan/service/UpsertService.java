package co.deepthought.quickscan.service;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * A service inserting or updating documents to the store.
 */
public class UpsertService
        extends BaseService<UpsertService.Input, ServiceSuccess> {

    public static class Input extends Validated {

        public String documentId;
        public String resultId;
        public String shardId;
        public String[] tags;
        public Map<String, Double> fields;
        public Map<String, Double> scores;
        public Input() {}

        @Override
        public void validate() throws ServiceFailure {
            this.validateNonNull(this.documentId, "documentId");
            this.validateNonNull(this.documentId, "resultId");
            this.validateNonNull(this.documentId, "shardId");
            this.validateNonNull(this.tags, "tags");
            for(final String tag : this.tags) {
                this.validateNonNull(tag, "tags[]");
            }
            this.validateNonNull(this.fields, "fields");
            for(final Double field : this.fields.values()) {
                this.validateNonNull(field, "fields[]");
            }
            this.validateNonNull(this.scores, "scores");
            for(final Double score : this.scores.values()) {
                this.validateNonNull(score, "scores[]");
            }
        }
    }

    private final DocumentStore documentStore;

    public UpsertService(final DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    @Override
    public Class<Input> getInputClass() {
        return Input.class;
    }

    @Override
    public ServiceSuccess handle(final Input input) throws ServiceFailure {
        try {
            this.documentStore.transaction(
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        UpsertService.this.documentStore.deleteById(input.documentId);
                        final Document document = UpsertService.this.documentStore.createDocument(
                            input.documentId, input.resultId, input.shardId);
                        for(final String tag : input.tags) {
                            document.addTag(tag);
                        }
                        for(final Map.Entry<String, Double> field : input.fields.entrySet()) {
                            document.addField(field.getKey(), field.getValue());
                        }
                        for(final Map.Entry<String, Double> score : input.scores.entrySet()) {
                            document.addScore(score.getKey(), score.getValue());
                        }
                        return null;
                    }
                }
            );
            return new ServiceSuccess();
        } catch (SQLException e) {
            // this is unlikely, why would this be a checked exception?
            throw new ServiceFailure("database error");
        }

    }

}