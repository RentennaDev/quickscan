package co.deepthought.quickscan.service;

import co.deepthought.quickscan.store.Result;
import co.deepthought.quickscan.store.ResultStore;
import com.sleepycat.je.DatabaseException;

/**
 * A service inserting or updating documents to the store.
 */
public class UpsertService
        extends BaseService<UpsertService.Input, ServiceSuccess> {

    public static class Input extends Validated {
        public Result result;
        public Input() {}

        @Override
        public void validate() throws ServiceFailure {
            this.validateNonNull(this.result, "result");
            this.validateNonNull(this.result.getId(), "result.id");
            this.validateNonNull(this.result.getShardId(), "result.shardId");
            this.validateNonNull(this.result.getFields(), "result.fields");
            this.validateNonNull(this.result.getScores(), "result.scores");
            this.validateNonNull(this.result.getTags(), "result.tags");
            this.validateNonNull(this.result.getDocuments(), "result.documents");
            // TODO: check each field, score, tag, document
        }

    }

    private final ResultStore resultStore;

    public UpsertService(final ResultStore resultStore) {
        this.resultStore = resultStore;
    }

    @Override
    public Class<Input> getInputClass() {
        return Input.class;
    }

    @Override
    public ServiceSuccess handle(final Input input) throws ServiceFailure {
        try {
            this.resultStore.persist(input.result);
            return new ServiceSuccess();
        } catch (DatabaseException e) {
            // this is unlikely, why would this be a checked exception?
            throw new ServiceFailure("database error");
        }

    }

}