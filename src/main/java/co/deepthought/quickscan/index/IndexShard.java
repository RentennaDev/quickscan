package co.deepthought.quickscan.index;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Stores a normalized dataset and provides scanning/sorting functionality.
 */
public class IndexShard {

    final static Logger LOGGER = Logger.getLogger(IndexShard.class.getCanonicalName());

    public static final long ALL_HOT = ~0L;
    public static final int SORTING_RESOLUTION = 256;

    private final int size;
    private final String[] resultIds;
    private final long[][] tags;
    private final double[][] minFields;
    private final double[][] maxFields;
    private final double[][] scores;

    public IndexShard(
            final String[] resultIds,
            final long[][] tags,
            final double[][] minFields,
            final double[][] maxFields,
            final double[][] scores
        ) {
        this.resultIds = resultIds;
        this.tags = tags;
        this.minFields = minFields;
        this.maxFields = maxFields;
        this.scores = scores;
        this.size = this.resultIds.length;
    }

    public boolean[] filter(
            final long[] conjunctiveTags,
            final long[][] disjunctiveTags,
            final double[] minFilters,
            final double[] maxFilters
        ) {
        final boolean[] nonmatches = new boolean[this.size];

        for(int i = 0; i < disjunctiveTags.length; i++) {
            this.filterDisjunctive(nonmatches, disjunctiveTags[i]);
        }

        for(int i = 0; i < this.tags.length; i++) {
            this.filterConjunctive(nonmatches, i, conjunctiveTags[i]);
        }

        for(int i = 0; i < this.minFields.length; i++) {
            this.filterMin(nonmatches, i, minFilters[i]);
        }

        for(int i = 0; i < this.maxFields.length; i++) {
            this.filterMax(nonmatches, i, maxFilters[i]);
        }

        return nonmatches;
    }

    public void filterConjunctive(final boolean[] nonmatches, final int index, final long comparison) {
        if(comparison != 0) {
            final long mask = ~comparison;
            final long[] values = this.tags[index];
            for(int i = 0; i < this.size; i++) {
                nonmatches[i] |= ((values[i] | mask) != IndexShard.ALL_HOT);
            }
        }
    }

    public void filterDisjunctive(final boolean[] nonmatches, final long[] comparison) {
        // because two disjunctive tags can spread multiple pages, we need all pages to make this reasonable
        final boolean[] submatches = new boolean[this.size];
        for(int page = 0; page < this.tags.length; page++) {
            final long mask = comparison[page];
            if(mask != 0L) {
                final long[] values = this.tags[page];
                for(int i = 0; i < this.size; i++) {
                    submatches[i] |= ((values[i] & mask) != 0L);
                }
            }
        }
        for(int i = 0; i < this.size; i++) {
            nonmatches[i] |= !(submatches[i]);
        }
    }

    public void filterMax(final boolean[] nonmatches, final int index, final double comparison) {
        if(!Double.isNaN(comparison)) {
            final double[] values = this.maxFields[index];
            for(int i = 0; i < this.size; i++) {
                nonmatches[i] |= (values[i] > comparison);
            }
        }
    }

    public void filterMin(final boolean[] nonmatches, final int index, final double comparison) {
        if(!Double.isNaN(comparison)) {
            final double[] values = this.minFields[index];
            for(int i = 0; i < this.size; i++) {
                nonmatches[i] |= (values[i] < comparison);
            }
        }
    }

    public List[] getBuckets() {
        final List[] buckets = new List[IndexShard.SORTING_RESOLUTION];
        for(int i = 0; i < IndexShard.SORTING_RESOLUTION; i++) {
            buckets[i] = new ArrayList();
        }
        return buckets;
    }


    public PaginatedResults<String> scan(
            final long[] conjunctiveTags,
            final long[][] disjunctiveTags,
            final double[] minFilters,
            final double[] maxFilters,
            final double[] preferences,
            final int number
        ) {
        final long start = System.nanoTime();

        final boolean[] nonmatches = this.filter(conjunctiveTags, disjunctiveTags, minFilters, maxFilters);
        final long filtered = System.nanoTime();
        LOGGER.info("filter in " + ((filtered-start)/1000) + "us");

        final List[] buckets = this.getBuckets();
        this.sortToBuckets(buckets, nonmatches, preferences);
        final PaginatedResults<String> results = this.trimBuckets(buckets, number);
        final long scored = System.nanoTime();
        LOGGER.info("score in " + ((scored-filtered)/1000) + "us");

        return results;
    }

    public void sortToBuckets(final List[] buckets, final boolean[] nonmatches, final double[] preferences) {
        for(int i = 0; i < this.size; i++) {
            if(!nonmatches[i]) {
                double total = 0;
                double weight = 0;

                final double[] score = scores[i];
                for(int j = 0; j < score.length; j++) {
                    if(score[j] >= 0) {
                        total += preferences[j] * score[j];
                        weight += preferences[j];
                    }
                }

                if(weight > 0) {
                    final int position = (int) ((1.0-total/weight) * IndexShard.SORTING_RESOLUTION);
                    buckets[position].add(this.resultIds[i]);
                }
            }
        }
    }

    public PaginatedResults<String> trimBuckets(final List[] buckets, final int number) {
        final Set<String> disinctIds = new HashSet<>();
        final List<String> resultIds = new ArrayList<>();
        for(final List bucket : buckets) {
            for(final Object item : bucket) {
                if(disinctIds.add((String)item)) {
                    if(resultIds.size() < number) {
                        resultIds.add((String)item);
                    }
                }
            }
        }
        return new PaginatedResults<>(resultIds, disinctIds.size());
    }


}