package co.deepthought.quickscan.server;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;
import com.sleepycat.je.DatabaseException;

/**
 * A service inserting or updating documents to the store.
 */
public class UpsertService
        extends BaseService<UpsertService.Input, ServiceSuccess> {

    public static class Input extends Validated {
        public Document document;
        public Input() {}

        @Override
        public void validate() throws ServiceFailure {
            this.validateNonNull(this.document, "document");
            this.validateNonNull(this.document.getId(), "document.id");
            this.validateNonNull(this.document.getShardId(), "document.shardId");
            this.validateNonNull(this.document.getFields(), "document.fields");
            this.validateNonNull(this.document.getScores(), "document.scores");
            this.validateNonNull(this.document.getTags(), "document.tags");
            // TODO: check each field, score, tag
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
            input.document.setDeprecated(false);
            this.documentStore.persist(input.document);
            return new ServiceSuccess();
        } catch (DatabaseException e) {
            // this is unlikely, why would this be a checked exception?
            throw new ServiceFailure("database error");
        }

    }
}