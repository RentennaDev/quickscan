package co.deepthought.quickscan.service;

import co.deepthought.quickscan.index.SearcherManager;

import java.sql.SQLException;

/**
 * A service for indexing a shard and hotswapping it.
 */
public class IndexService
        extends BaseService<IndexService.Input, ServiceSuccess>  {

    private final SearcherManager manager;

    public static class Input extends Validated {

        public String shardId;

        @Override
        public void validate() throws ServiceFailure {}

    }

    public IndexService(final SearcherManager manager) {
        this.manager = manager;
    }

    @Override
    public Class<Input> getInputClass() {
        return Input.class;
    }

    @Override
    public ServiceSuccess handle(final Input input) throws ServiceFailure {
        try {
            if(input.shardId == null) {
                this.manager.index();
            }
            else {
                this.manager.indexShard(input.shardId);
            }
            return new ServiceSuccess();
        } catch (SQLException e) {
            // this is unlikely, why would this be a checked exception?
            throw new ServiceFailure("database error");
        }
    }
}
