package co.deepthought.quickscan.index;

import java.util.Map;

public class SearchResult {

    private final String resultId;
    private final String payload;
    private final Map<String, Double> scores;

    public SearchResult(final String resultId, final String payload, final Map<String, Double> scores) {
        this.resultId = resultId;
        this.payload = payload;
        this.scores = scores;
    }

    public String getResultId() {
        return this.resultId;
    }

    public String getPayload() {
        return this.payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchResult that = (SearchResult) o;

        if (resultId != null ? !resultId.equals(that.resultId) : that.resultId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return resultId != null ? resultId.hashCode() : 0;
    }
}