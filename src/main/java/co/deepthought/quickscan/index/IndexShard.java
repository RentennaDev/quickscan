package co.deepthought.quickscan.index;

import java.util.Arrays;

public class IndexShard {

    public static final long ALL_HOT = ~0L;

    private final int size;
    private final String[] resultIds;
    private final long[][] tags;
    private final double[][] fields;
    private final double[][] neutralScores;
    private final double[][] negativeScores;
    private final double[][] positiveScores;

    public IndexShard(
            final String[] resultIds,
            final long[][] tags,
            final double[][] fields,
            final double[][] neutralScores,
            final double[][] negativeScores,
            final double[][] positiveScores
        ) {
        this.resultIds = resultIds;
        this.tags = tags;
        this.fields = fields;
        this.neutralScores = neutralScores;
        this.negativeScores = negativeScores;
        this.positiveScores = positiveScores;
        this.size = this.resultIds.length;
    }

    public String[] scan(
            final long[] disjunctiveTags,
            final long[][] conjunctiveTags,
            final double[] minFilters,
            final double[] maxFilters,
            final double[] neutralPreferences,
            final double[] negativePreferences,
            final double[] postivePreferences,
            final int number
        ) {
        final boolean[] nonmatches = this.filter(disjunctiveTags, conjunctiveTags, minFilters, maxFilters);

        return null;
    }

    final boolean[] filter(
            final long[] disjunctiveTags,
            final long[][] conjunctiveTags,
            final double[] minFilters,
            final double[] maxFilters
        ) {
        final boolean[] nonmatches = new boolean[this.size];

        return nonmatches;
    }

    final void filterConjunctive(boolean[] nonmatches, final int index, final long comparison) {
        if(comparison != 0) {
            final long mask = ~comparison;
            final long[] values = this.tags[index];
            for(int i = 0; i < this.size; i++) {
                nonmatches[i] |= ((values[i] | mask) != IndexShard.ALL_HOT);
            }
        }
    }

    final void filterDisjunctive(boolean[] nonmatches, final long[] comparison) {
        // because two disjunctive tags can spread multiple pages, we need all pages to make this reasonable
        final boolean[] submatches = new boolean[this.size];
        for(int page = 0; page < this.tags.length; page++) {
            final long mask = comparison[page];
            final long[] values = this.tags[page];
            for(int i = 0; i < this.size; i++) {
                submatches[i] |= ((values[i] & mask) != 0L);
            }
        }
        for(int i = 0; i < this.size; i++) {
            nonmatches[i] |= !(submatches[i]);
        }
    }

    final void filterMax(boolean[] nonmatches, final int index, final double comparison) {
        if(!Double.isNaN(comparison)) {
            final double[] values = this.fields[index];
            for(int i = 0; i < this.size; i++) {
                nonmatches[i] |= (values[i] > comparison);
            }
        }
    }

    final void filterMin(boolean[] nonmatches, final int index, final double comparison) {
        if(!Double.isNaN(comparison)) {
            final double[] values = this.fields[index];
            for(int i = 0; i < this.size; i++) {
                nonmatches[i] |= (values[i] < comparison);
            }
        }
    }

}
