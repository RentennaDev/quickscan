package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;
import co.deepthought.quickscan.store.Score;

import java.sql.SQLException;

public class Indexer {

    final private DocumentStore store;

    public Indexer(final DocumentStore store) {
        this.store = store;
    }

    public IndexShard index(final String shardId) throws SQLException {
        final IndexMapper indexMapper = new IndexMapper(
            this.store.getDistinctTags(shardId),
            this.store.getDistinctFields(shardId),
            this.store.getDistinctScores(shardId, Score.Valence.NEUTRAL),
            this.store.getDistinctScores(shardId, Score.Valence.NEGATIVE),
            this.store.getDistinctScores(shardId, Score.Valence.POSITIVE)
        );

        final IndexNormalizer normalizer = new IndexNormalizer(indexMapper);
        for(final Document document : this.store.getDocuments(shardId)) {
            normalizer.indexDocument(document);
        }

        return normalizer.normalize();
    }

}