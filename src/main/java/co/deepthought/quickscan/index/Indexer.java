package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Result;
import co.deepthought.quickscan.store.ResultStore;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import org.apache.log4j.Logger;

/**
 * Can produce Searchers for various shards.
 */
public class Indexer {

    final static Logger LOGGER = Logger.getLogger(Indexer.class.getCanonicalName());

    final private ResultStore store;

    public Indexer(final ResultStore store) {
        this.store = store;
    }

    public Searcher index(final String shardId) throws DatabaseException {
        final long start = System.currentTimeMillis();
        final IndexMap indexMap = this.map(shardId);
        final IndexShard shard = this.normalize(indexMap, shardId);
        final long end = System.currentTimeMillis();
        LOGGER.info("indexed " + shardId + " in " + (end-start) + "ms");
        return new Searcher(indexMap, shard, this.store);
    }

    private IndexMap map(final String shardId) throws DatabaseException {
        final IndexMapper indexMapper = new IndexMapper();
        final EntityCursor<Result> cursor = this.store.getByShardId(shardId);
        try {
            for(final Result result : cursor) {
                indexMapper.inspect(result);
            }
        }
        finally {
            cursor.close();
        }
        return indexMapper.map();
    }

    private IndexShard normalize(final IndexMap indexMap, final String shardId) throws DatabaseException {
        final IndexNormalizer normalizer = new IndexNormalizer(indexMap);
        final EntityCursor<Result> cursor = this.store.getByShardId(shardId);
        try {
            for(final Result result : cursor) {
                normalizer.index(result);
            }
        }
        finally {
            cursor.close();
        }
        return normalizer.normalize();
    }

}