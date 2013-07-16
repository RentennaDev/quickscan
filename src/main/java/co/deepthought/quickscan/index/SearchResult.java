package co.deepthought.quickscan.index;

public class SearchResult implements Comparable<SearchResult> {

    private final String resultId;
    private final double[] scores;
    private final double score;

    public SearchResult(final String resultId, final double[] scores, final double score) {
        this.resultId = resultId;
        this.scores = scores;
        this.score = score;
    }

    @Override
    public int compareTo(final SearchResult other) {
        if(other.score > this.score) {
            return -1;
        }
        else if(other.score < this.score) {
            return 1;
        }
        else {
            return 0;
        }
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