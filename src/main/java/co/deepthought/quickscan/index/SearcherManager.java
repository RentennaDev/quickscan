package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Result;
import co.deepthought.quickscan.store.ResultStore;
import com.sleepycat.je.DatabaseException;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a synchronized mapping of the various searchers. This class is thread-safe.
 */
public class SearcherManager {

    final private ResultStore store;
    final private Indexer indexer;
    final private Map<String, Searcher> searchers;

    public SearcherManager(final ResultStore store) {
        this.store = store;
        this.indexer = new Indexer(store);
        this.searchers = new HashMap<>();
    }

    public SearchResult getSearchResult(final String resultId) throws DatabaseException {
        final Result result = this.store.getById(resultId);
        if(result == null) {
            return null;
        }

        final Searcher searcher = this.getSearcher(result.getShardId());
        if(searcher == null) {
            return null;
        }

        return searcher.getSearchResult(result);
    }

    public synchronized Searcher getSearcher(final String shardId) {
        return this.searchers.get(shardId);
    }

    public void index() throws DatabaseException {
        for(final String shardId : this.store.getDistinctShardIds()) {
            this.indexShard(shardId);
        }
    }
    public void indexShard(final String shardId) throws DatabaseException {
        final Searcher searcher = this.indexer.index(shardId);
        this.setSearcher(shardId, searcher);
    }

    private synchronized void setSearcher(final String shardId, final Searcher searcher) {
        this.searchers.put(shardId, searcher);
    }

}