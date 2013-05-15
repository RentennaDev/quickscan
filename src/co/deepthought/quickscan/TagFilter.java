package co.deepthought.quickscan;

/**
 * A comparison filter on a particular field
 */
public class TagFilter {

    protected final long mask;
    protected final int tagIndex;

    public TagFilter(final int tagIndex, final long mask) {
        this.mask = mask;
        this.tagIndex = tagIndex;
    }

}