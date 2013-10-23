package co.deepthought.quickscan.server;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;
import com.sleepycat.je.DatabaseException;

import java.util.List;

/**
 * A service deleting documents from the store.
 */
public class DeprecateService
        extends BaseService<DeprecateService.Input, ServiceSuccess> {

    public static class Input extends Validated {
        public List<String> ids;

        @Override
        public void validate() throws ServiceFailure {
            this.validateNonNull(this.ids, "ids");
        }
    }

    private final DocumentStore documentStore;

    public DeprecateService(final DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    @Override
    public Class<Input> getInputClass() {
        return Input.class;
    }

    @Override
    public ServiceSuccess handle(final Input input) throws ServiceFailure {
        try {
            for(final String id : input.ids) {
                final Document document = this.documentStore.getById(id);
                if(document != null) {
                    document.setDeprecated(true);
                }
                this.documentStore.persist(document);
            }
            return new ServiceSuccess();
        } catch (DatabaseException e) {
            // this is unlikely, why would this be a checked exception?
            throw new ServiceFailure("database error");
        }
    }

}