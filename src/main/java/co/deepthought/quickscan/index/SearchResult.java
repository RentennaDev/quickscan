package co.deepthought.quickscan.index;

public class SearchResult {

    private final String resultId;
    private final double[] scores;

    public SearchResult(final String resultId, final double[] scores) {
        this.resultId = resultId;
        this.scores = scores;
    }

    @Override
    public boolean equals(final Object other) {
        return this.resultId.equals(((SearchResult)other).resultId);
    }

    @Override
    public int hashCode() {
        return this.resultId.hashCode();
    }

    public String getResultId() {
        return this.resultId;
    }

    public double[] getScores() {
        return this.scores;
    }
}
