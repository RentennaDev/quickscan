package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;
import com.sleepycat.je.DatabaseException;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Maintains both an IndexMap and an IndexShard and can perform searches on the dataset, yielding document ids.
 */
public class Searcher {

    final static Logger LOGGER = Logger.getLogger(Searcher.class.getCanonicalName());

    private final IndexMap indexMap;
    private final IndexShard indexShard;
    private final DocumentStore documentStore;

    public Searcher(final IndexMap indexMap, final IndexShard indexShard, final DocumentStore documentStore) {
        this.indexMap = indexMap;
        this.indexShard = indexShard;
        this.documentStore = documentStore;
    }

    public SearchResult getSearchResult(final Document document) {
        final Map<String, Double> projectedScores = this.indexMap.projectScores(document.getScoreValues());
        return new SearchResult(document.getId(), document.getPayload(), projectedScores);
    }

    public PaginatedResults<SearchResult> search(
            final List<String> conjunctiveTags,
            final List<List<String>> disjunctiveTags,
            final Map<String, Double> minFilters,
            final Map<String, Double> maxFilters,
            final Map<String, Double> preferences,
            final int limit,
            final int skip
        ) throws DatabaseException {
        final PaginatedResults<String> searchResults = this.indexShard.scan(
            this.indexMap.normalizeTags(conjunctiveTags),
            this.normalizeDisjunctiveTags(disjunctiveTags),
            this.indexMap.normalizeFields(minFilters, Double.NaN),
            this.indexMap.normalizeFields(maxFilters, Double.NaN),
            this.indexMap.normalizeScores(preferences, 1.0, false),
            limit + skip
        );

        final long start = System.nanoTime();
        final List<SearchResult> results = new ArrayList<>();
        int count = 0;
        for(final String resultId : searchResults.getResults()) {
            if(count >= skip) {
                results.add(this.getSearchResult(this.documentStore.getById(resultId)));
            }
            count++;
        }
        final long end = System.nanoTime();
        LOGGER.info("retrieved payloads in " + ((end-start)/1000) + "us");

        return new PaginatedResults<>(results, searchResults.getTotal());
    }

    public long[][] normalizeDisjunctiveTags(final List<List<String>> tagSets) {
        final List<long[]> normalizedTagSets = new ArrayList<>();
        for(final List<String> tags : tagSets) {
            normalizedTagSets.add(this.indexMap.normalizeTags(tags));
        }
        return normalizedTagSets.toArray(new long[normalizedTagSets.size()][]);
    }

}