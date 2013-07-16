package co.deepthought.quickscan.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Maintains both an IndexMapper and an IndexShard and can perform searches on the dataset, yielding result ids.
 */
public class Searcher {

    private final IndexMapper indexMapper;
    private final IndexShard indexShard;

    public Searcher(final IndexMapper indexMapper, final IndexShard indexShard) {
        this.indexMapper = indexMapper;
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
            this.indexMapper.normalizeTags(conjunctiveTags),
            this.normalizeDisjunctiveTags(disjunctiveTags),
            this.indexMapper.normalizeFields(minFilters, Double.NaN),
            this.indexMapper.normalizeFields(maxFilters, Double.NaN),
            this.indexMapper.normalizeScores(preferences, 1.0),
            limit
        );
    }

    public long[][] normalizeDisjunctiveTags(final List<List<String>> tagSets) {
        final List<long[]> normalizedTagSets = new ArrayList<long[]>();
        for(final List<String> tags : tagSets) {
            normalizedTagSets.add(this.indexMapper.normalizeTags(tags));
        }
        return normalizedTagSets.toArray(new long[normalizedTagSets.size()][]);
    }

}
