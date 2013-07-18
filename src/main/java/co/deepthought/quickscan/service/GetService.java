package co.deepthought.quickscan.service;

import co.deepthought.quickscan.index.SearchResult;
import co.deepthought.quickscan.index.SearcherManager;
import com.sleepycat.je.DatabaseException;

/**
 * A service retrieving a single document
 */
public class GetService
        extends BaseService<GetService.Input, GetService.Output> {

    public static class Input extends Validated {
        public String id;

        @Override
        public void validate() throws ServiceFailure {
            this.validateNonNull(this.id, "id");
        }

        public Input() {}
    }

    public static class Output {
        public String status = "success";
        public SearchResult result;
        public Output() {}
    }

    private final SearcherManager manager;

    public GetService(final SearcherManager manager) {
        this.manager = manager;
    }

    @Override
    public Class<Input> getInputClass() {
        return Input.class;
    }

    @Override
    public Output handle(final Input input) throws ServiceFailure {
        try {
            final SearchResult result = this.manager.getSearchResult(input.id);
            if(result == null) {
                throw new ServiceFailure("result not indexed");
            }
            else {
                final Output output = new Output();
                output.result = result;
                return output;
            }
        } catch (DatabaseException e) {
            throw new ServiceFailure("database error");
        }
    }

}