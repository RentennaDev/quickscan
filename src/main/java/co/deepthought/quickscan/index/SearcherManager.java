package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;
import com.sleepycat.je.DatabaseException;

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
        this.searchers = new HashMap<>();
    }

    public SearchResult getSearchResult(final String resultId) throws DatabaseException {
        final Document document = this.store.getById(resultId);
        if(document == null) {
            return null;
        }

        final Searcher searcher = this.getSearcher(document.getShardId());
        if(searcher == null) {
            return null;
        }

        return searcher.getSearchResult(document);
    }

    public synchronized Searcher getSearcher(final String shardId) {
        return this.searchers.get(shardId);
    }

    public void index() throws DatabaseException {
        for(final String shardId : this.store.getDistinctShardIds()) {
            System.out.println("Indexing: " + shardId);
            final long start = System.currentTimeMillis();
            final int count = this.indexShard(shardId);
            final long end = System.currentTimeMillis();
            System.out.println("Indexed " + count + " documents in " + (end-start));
        }
    }
    public int indexShard(final String shardId) throws DatabaseException {
        final Searcher searcher = this.indexer.index(shardId);
        this.setSearcher(shardId, searcher);
        return searcher.getSize();
    }

    private synchronized void setSearcher(final String shardId, final Searcher searcher) {
        this.searchers.put(shardId, searcher);
    }

}