package co.deepthought.quickscan.index;

import java.util.Collection;

public class PaginatedResults<ResultType> {

    private final Collection<ResultType> results;
    private final int total;

    public PaginatedResults(final Collection<ResultType> results, final int total) {
        this.results = results;
        this.total = total;
    }

    public Collection<ResultType> getResults() {
        return this.results;
    }

    public int getTotal() {
        return this.total;
    }
}
