package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Result;
import co.deepthought.quickscan.store.ResultStore;
import com.sleepycat.je.DatabaseException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Maintains both an IndexMap and an IndexShard and can perform searches on the dataset, yielding result ids.
 */
public class Searcher {

    final static Logger LOGGER = Logger.getLogger(Searcher.class.getCanonicalName());

    private final IndexMap indexMap;
    private final IndexShard indexShard;
    private final ResultStore resultStore;

    public Searcher(final IndexMap indexMap, final IndexShard indexShard, final ResultStore resultStore) {
        this.indexMap = indexMap;
        this.indexShard = indexShard;
        this.resultStore = resultStore;
    }

    public Collection<SearchResult> search(
            final List<String> conjunctiveTags,
            final List<List<String>> disjunctiveTags,
            final Map<String, Double> minFilters,
            final Map<String, Double> maxFilters,
            final Map<String, Double> preferences,
            final int limit
        ) throws DatabaseException {

        final Collection<SearchResult> searchResults = this.indexShard.scan(
            this.indexMap.normalizeTags(conjunctiveTags),
            this.normalizeDisjunctiveTags(disjunctiveTags),
            this.indexMap.normalizeFields(minFilters, Double.NaN),
            this.indexMap.normalizeFields(maxFilters, Double.NaN),
            this.indexMap.normalizeScores(preferences, 1.0),
            limit
        );

        final long start = System.nanoTime();
        for(final SearchResult searchResult : searchResults) {
            final Result result = this.resultStore.getById(searchResult.getResultId());
            searchResult.setPayload(result.getPayload());
            // TODO: map scores!
        }
        final long end = System.nanoTime();
        LOGGER.info("retrieved payloads in " + (end-start) + "ns");

        return searchResults;
    }

    public long[][] normalizeDisjunctiveTags(final List<List<String>> tagSets) {
        final List<long[]> normalizedTagSets = new ArrayList<>();
        for(final List<String> tags : tagSets) {
            normalizedTagSets.add(this.indexMap.normalizeTags(tags));
        }
        return normalizedTagSets.toArray(new long[normalizedTagSets.size()][]);
    }

}