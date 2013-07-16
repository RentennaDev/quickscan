package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;

import java.sql.SQLException;

/**
 * Can produce Searchers for various shards.
 */
public class Indexer {

    final private DocumentStore store;

    public Indexer(final DocumentStore store) {
        this.store = store;
    }

    public Searcher index(final String shardId) throws SQLException {
        // first pass to produce the map
        final IndexMapper indexMapper = new IndexMapper();
        for(final Document document : this.store.getDocuments(shardId)) {
            indexMapper.inspect(document);
        }
        final IndexMap indexMap = indexMapper.map();

        // second pass to index the data
        final IndexNormalizer normalizer = new IndexNormalizer(indexMap);
        for(final Document document : this.store.getDocuments(shardId)) {
            normalizer.indexDocument(document);
        }

        final IndexShard shard = normalizer.normalize();
        return new Searcher(indexMap, shard);
    }

}