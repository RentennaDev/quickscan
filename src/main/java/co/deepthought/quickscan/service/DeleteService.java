package co.deepthought.quickscan.service;

import co.deepthought.quickscan.store.ResultStore;
import com.sleepycat.je.DatabaseException;

/**
 * A service deleting documents from the store.
 */
public class DeleteService
        extends BaseService<DeleteService.Input, ServiceSuccess> {

    public static class Input extends Validated {
        public String id;

        @Override
        public void validate() throws ServiceFailure {
            this.validateNonNull(this.id, "id");
        }
    }

    private final ResultStore resultStore;

    public DeleteService(final ResultStore resultStore) {
        this.resultStore = resultStore;
    }

    @Override
    public Class<Input> getInputClass() {
        return Input.class;
    }

    @Override
    public ServiceSuccess handle(final Input input) throws ServiceFailure {
        try {
            this.resultStore.deleteById(input.id);
            return new ServiceSuccess();
        } catch (DatabaseException e) {
            // this is unlikely, why would this be a checked exception?
            throw new ServiceFailure("database error");
        }
    }

}