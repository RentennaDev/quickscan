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
        final IndexMapper indexMapper = new IndexMapper(
            this.store.getDistinctTags(shardId),
            this.store.getDistinctFields(shardId),
            this.store.getDistinctScores(shardId)
        );

        final IndexNormalizer normalizer = new IndexNormalizer(indexMapper);
        for(final Document document : this.store.getDocuments(shardId)) {
            normalizer.indexDocument(document);
        }

        final IndexShard shard = normalizer.normalize();
        return new Searcher(indexMapper, shard);
    }

}