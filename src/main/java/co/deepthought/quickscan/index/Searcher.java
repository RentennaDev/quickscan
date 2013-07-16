package co.deepthought.quickscan.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Maintains both an IndexMap and an IndexShard and can perform searches on the dataset, yielding result ids.
 */
public class Searcher {

    private final IndexMap indexMap;
    private final IndexShard indexShard;

    public Searcher(final IndexMap indexMap, final IndexShard indexShard) {
        this.indexMap = indexMap;
        this.indexShard = indexShard;
    }

    public Collection<SearchResult> search(
            final List<String> conjunctiveTags,
            final List<List<String>> disjunctiveTags,
            final Map<String, Double> minFilters,
            final Map<String, Double> maxFilters,
            final Map<String, Double> preferences,
            final int limit
        ) {
        return this.indexShard.scan(
            this.indexMap.normalizeTags(conjunctiveTags),
            this.normalizeDisjunctiveTags(disjunctiveTags),
            this.indexMap.normalizeFields(minFilters, Double.NaN),
            this.indexMap.normalizeFields(maxFilters, Double.NaN),
            this.indexMap.normalizeScores(preferences, 1.0),
            limit
        );
    }

    public long[][] normalizeDisjunctiveTags(final List<List<String>> tagSets) {
        final List<long[]> normalizedTagSets = new ArrayList<long[]>();
        for(final List<String> tags : tagSets) {
            normalizedTagSets.add(this.indexMap.normalizeTags(tags));
        }
        return normalizedTagSets.toArray(new long[normalizedTagSets.size()][]);
    }

}
