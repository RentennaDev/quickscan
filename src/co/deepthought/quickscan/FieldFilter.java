package co.deepthought.quickscan;

/**
 * A comparison filter on a particular field
 */
public class FieldFilter {

    protected final int comparisonValue;
    protected final int fieldIndex;

    public FieldFilter(final int fieldIndex, final int comparisonValue) {
        this.comparisonValue = comparisonValue;
        this.fieldIndex = fieldIndex;
    }

}
