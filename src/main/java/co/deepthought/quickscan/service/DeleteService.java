package co.deepthought.quickscan.service;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;

import java.sql.SQLException;
import java.util.Map;

/**
 * A service deleting documents from the store.
 */
public class DeleteService
        extends BaseService<DeleteService.Input, ServiceSuccess> {

    public static class Input extends Validated {
        public String documentId;
        @Override
        public void validate() throws ServiceFailure {
            this.validateNonNull(this.documentId, "documentId");
        }
    }

    private final DocumentStore documentStore;

    public DeleteService(final DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    @Override
    public Class<Input> getInputClass() {
        return Input.class;
    }

    @Override
    public ServiceSuccess handle(final Input input) throws ServiceFailure {
        try {
            this.documentStore.deleteById(input.documentId);
            return new ServiceSuccess();
        } catch (SQLException e) {
            // this is unlikely, why would this be a checked exception?
            throw new ServiceFailure("database error");
        }
    }

}