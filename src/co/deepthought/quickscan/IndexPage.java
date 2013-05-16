package co.deepthought.quickscan;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An index page contains some scannable chunk of document index data
 */
public class IndexPage {

    public static final long ALL_HOT = ~0L;

    protected final String[] indexIdentifiers;
    protected final int[][] indexFields;
    protected final long[][] indexTags;
    // TODO: scores
    protected final int dataBoundary;

    public IndexPage(
            final int page,
            final String[] indexIdentifiers,
            final int[][] indexFields,
            final long[][] indexTags) {
        final int pageStart = page * Index.PAGE_SIZE;
        final int pageEnd = pageStart + Index.PAGE_SIZE;
        this.dataBoundary = Math.min(pageEnd, indexIdentifiers.length) - pageStart;

        this.indexIdentifiers = Arrays.copyOfRange(indexIdentifiers, pageStart, pageEnd);

        this.indexFields = new int[indexFields.length][];
        for(int fieldIndex = 0; fieldIndex < indexFields.length; fieldIndex++) {
            this.indexFields[fieldIndex] = Arrays.copyOfRange(indexFields[fieldIndex], pageStart, pageEnd);
        }

        this.indexTags = new long[indexTags.length][];
        for(int tagIndex = 0; tagIndex < indexTags.length; tagIndex++) {
            this.indexTags[tagIndex] = Arrays.copyOfRange(indexTags[tagIndex], pageStart, pageEnd);
        }

        // TODO: get the score range
    }

    /**
     * TODO: need to figure out a proper signature
     * TODO: figure out how to parllelize if necessary
     * @return the set of matching identifiers
     */
    public Set<String> scan(final NormalizedQuery query) {
        long start, end;

        start = System.nanoTime();
        final boolean[] matches = this.filter(query);
        end = System.nanoTime();
        System.out.println("scantime: " + (end-start));

        final Set<String> matching = new HashSet<String>();
        for(int i = 0; i < Index.PAGE_SIZE; i++) {
            if(matches[i]) {
                matching.add(this.indexIdentifiers[i]);
            }
        }
        return matching;
    }

    private boolean[] filter(final NormalizedQuery query) {
        final boolean[] matches = new boolean[Index.PAGE_SIZE];
        Arrays.fill(matches, 0, this.dataBoundary, true); // assume true

        for(final FieldFilter filter : query.maxFilters) {
            this.filterMax(matches, filter);
        }

        for(final FieldFilter filter : query.minFilters) {
            this.filterMin(matches, filter);
        }

        for(final TagFilter filter : query.conjunctiveTagFilters) {
            this.fitlerConjunctive(matches, filter);
        }

        for(final List<TagFilter> filters : query.disjunctiveTagFilters) {
            this.fitlerDisjunctive(matches, filters);
        }

        return matches;
    }

    private void fitlerConjunctive(final boolean[] matches, final TagFilter filter) {
        final long mask = ~filter.mask;
        final long[] values = this.indexTags[filter.tagIndex];
        for(int i = 0; i < Index.PAGE_SIZE; i++) {
            matches[i] &= ((values[i] | mask) == IndexPage.ALL_HOT);
        }
    }

    private void fitlerDisjunctive(final boolean[] matches, final List<TagFilter> filters) {
        final boolean[] submatches = new boolean[Index.PAGE_SIZE]; // TODO: measure performance, this could be costly
        for(final TagFilter filter : filters) {
            final long mask = filter.mask;
            final long[] values = this.indexTags[filter.tagIndex];
            for(int i = 0; i < Index.PAGE_SIZE; i++) {
                submatches[i] |= ((values[i] & mask) != 0L);
            }
        }
        // TODO: here's a place where the bitset could make us faster by a factor of 64!
        for(int i = 0; i < Index.PAGE_SIZE; i++) {
            matches[i] &= submatches[i];
        }
    }

    private void filterMax(final boolean[] matches, final FieldFilter filter) {
        final int comparison = filter.comparisonValue;
        final int[] values = this.indexFields[filter.fieldIndex];
        for(int i = 0; i < Index.PAGE_SIZE; i++) {
            matches[i] &= (values[i] <= comparison);
        }
    }

    private void filterMin(final boolean[] matches, final FieldFilter filter) {
        final int comparison = filter.comparisonValue;
        final int[] values = this.indexFields[filter.fieldIndex];
        for(int i = 0; i < Index.PAGE_SIZE; i++) {
            matches[i] &= (values[i] >= comparison);
        }
    }

}