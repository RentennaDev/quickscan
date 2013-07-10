//package co.deepthought.quickscan;
//
//import java.util.*;
//
///**
// * An index page contains some scannable chunk of document index data
// */
//public class IndexPage {
//
//    public static final long ALL_HOT = ~0L;
//
//    protected final String[] indexIdentifiers;
//    protected final int[][] indexFields;
//    protected final long[][] indexTags;
//    protected final double[] indexScores;
//    protected final int dataBoundary;
//    protected final int scoreCount;
//
//    public IndexPage(
//            final int page,
//            final String[] indexIdentifiers,
//            final int[][] indexFields,
//            final long[][] indexTags,
//            final double[] indexScores,
//            final int scoreCount) {
//        final int pageStart = page * Index.PAGE_SIZE;
//        final int pageEnd = pageStart + Index.PAGE_SIZE;
//        this.dataBoundary = Math.min(pageEnd, indexIdentifiers.length) - pageStart;
//
//        this.indexIdentifiers = Arrays.copyOfRange(indexIdentifiers, pageStart, pageEnd);
//
//        this.indexFields = new int[indexFields.length][];
//        for(int fieldIndex = 0; fieldIndex < indexFields.length; fieldIndex++) {
//            this.indexFields[fieldIndex] = Arrays.copyOfRange(indexFields[fieldIndex], pageStart, pageEnd);
//        }
//
//        this.indexTags = new long[indexTags.length][];
//        for(int tagIndex = 0; tagIndex < indexTags.length; tagIndex++) {
//            this.indexTags[tagIndex] = Arrays.copyOfRange(indexTags[tagIndex], pageStart, pageEnd);
//        }
//
//        this.scoreCount = scoreCount;
//        this.indexScores = Arrays.copyOfRange(indexScores, 2 * scoreCount * pageStart, 2 * scoreCount * pageEnd);
//    }
//
//    /**
//     * Scan into the provided buckets.
//     */
//    public void scan(final NormalizedQuery query, final List<String>[] results) {
//        final boolean[] matches = this.filter(query);
//
//        for(int documentIndex = 0; documentIndex < Index.PAGE_SIZE; documentIndex++) {
//            if(matches[documentIndex]) {
//                final int startIndex = 2 * scoreCount * documentIndex;
//                double scoreSum = 0;
//                double weightSum = 0;
//                for(int scoreIndex = 0; scoreIndex < this.scoreCount; scoreIndex++) {
//                    final int offset = 2 * scoreIndex;
//                    final double weight = this.indexScores[offset + startIndex + 1] * query.preferences[scoreIndex];
//                    weightSum += weight;
//                    scoreSum += weight * this.indexScores[offset + startIndex];
//                }
//                if(weightSum > 0) {
//                    final int bucket = (int)(Index.SCORE_BUCKETS * scoreSum / weightSum); // hopefully never above 1!
//                    results[bucket].add(this.indexIdentifiers[documentIndex]);
//                }
//                else {
//                    results[0].add(this.indexIdentifiers[documentIndex]);
//                }
//            }
//        }
//    }
//
//    private boolean[] filter(final NormalizedQuery query) {
//        final boolean[] matches = new boolean[Index.PAGE_SIZE];
//        Arrays.fill(matches, 0, this.dataBoundary, true); // assume true
//
//        for(final FieldFilter filter : query.maxFilters) {
//            this.filterMax(matches, filter);
//        }
//
//        for(final FieldFilter filter : query.minFilters) {
//            this.filterMin(matches, filter);
//        }
//
//        for(final TagFilter filter : query.conjunctiveTagFilters) {
//            this.fitlerConjunctive(matches, filter);
//        }
//
//        for(final List<TagFilter> filters : query.disjunctiveTagFilters) {
//            this.fitlerDisjunctive(matches, filters);
//        }
//
//        return matches;
//    }
//
//    private void fitlerConjunctive(final boolean[] matches, final TagFilter filter) {
//        final long mask = ~filter.mask;
//        final long[] values = this.indexTags[filter.tagIndex];
//        for(int i = 0; i < Index.PAGE_SIZE; i++) {
//            matches[i] &= ((values[i] | mask) == IndexPage.ALL_HOT);
//        }
//    }
//
//    private void fitlerDisjunctive(final boolean[] matches, final List<TagFilter> filters) {
//        final boolean[] submatches = new boolean[Index.PAGE_SIZE];
//        for(final TagFilter filter : filters) {
//            final long mask = filter.mask;
//            final long[] values = this.indexTags[filter.tagIndex];
//            for(int i = 0; i < Index.PAGE_SIZE; i++) {
//                submatches[i] |= ((values[i] & mask) != 0L);
//            }
//        }
//        for(int i = 0; i < Index.PAGE_SIZE; i++) {
//            matches[i] &= submatches[i];
//        }
//    }
//
//    private void filterMax(final boolean[] matches, final FieldFilter filter) {
//        final int comparison = filter.comparisonValue;
//        final int[] values = this.indexFields[filter.fieldIndex];
//        for(int i = 0; i < Index.PAGE_SIZE; i++) {
//            matches[i] &= (values[i] <= comparison);
//        }
//    }
//
//    private void filterMin(final boolean[] matches, final FieldFilter filter) {
//        final int comparison = filter.comparisonValue;
//        final int[] values = this.indexFields[filter.fieldIndex];
//        for(int i = 0; i < Index.PAGE_SIZE; i++) {
//            matches[i] &= (values[i] >= comparison);
//        }
//    }
//
//}