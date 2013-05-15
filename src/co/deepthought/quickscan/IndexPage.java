package co.deepthought.quickscan;

import java.util.Arrays;

/**
 * An index page contains some scannable chunk of document index data
 */
public class IndexPage {

    private final String[] indexIdentifiers;
    private final int[][] indexFields;
    private final long[][] indexTags;
    // TODO: scores

    public IndexPage(
            final int page,
            final String[] indexIdentifiers,
            final int[][] indexFields,
            final long[][] indexTags) {
        final int pageStart = page * Index.PAGE_SIZE;
        final int pageEnd = pageStart + Index.PAGE_SIZE;

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

}