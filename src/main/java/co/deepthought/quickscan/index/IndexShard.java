package co.deepthought.quickscan.index;

public class IndexShard {

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
    }

}
