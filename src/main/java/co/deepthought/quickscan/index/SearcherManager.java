package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.DocumentStore;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a synchronized mapping of the various searchers. This class is thread-safe.
 */
public class SearcherManager {

    final private DocumentStore store;
    final private Indexer indexer;
    final private Map<String, Searcher> searchers;

    public SearcherManager(final DocumentStore store) {
        this.store = store;
        this.indexer = new Indexer(store);
        this.searchers = new HashMap<String, Searcher>();
    }

    public void index() throws SQLException {
        for(final String shardId : this.store.getDistinctShards()) {
            this.indexShard(shardId);
        }
    }

    public void indexShard(final String shardId) throws SQLException {
        final Searcher searcher = this.indexer.index(shardId);
        this.setSearcher(shardId, searcher);
    }

    public synchronized Searcher getSearcher(final String shardId) {
        return this.searchers.get(shardId);
    }

    private synchronized void setSearcher(final String shardId, final Searcher searcher) {
        this.searchers.put(shardId, searcher);
    }

}