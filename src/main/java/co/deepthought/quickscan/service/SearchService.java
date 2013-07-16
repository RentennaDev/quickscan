package co.deepthought.quickscan.service;

import co.deepthought.quickscan.index.SearchResult;
import co.deepthought.quickscan.index.Searcher;
import co.deepthought.quickscan.index.SearcherManager;
import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A service inserting or updating documents to the store.
 */
public class SearchService
        extends BaseService<SearchService.Input, SearchService.Output> {

    public static class Input extends Validated {
        public String shardId;
        public List<String> conjunctiveTags;
        public List<List<String>> disjunctiveTags;
        public Map<String, Double> minFilters;
        public Map<String, Double> maxFilters;
        public Map<String, Double> preferences;
        public int limit;

        @Override
        public void validate() throws ServiceFailure {
            this.validateNonNull(this.shardId, "shardId");
            this.validateNonNull(this.conjunctiveTags, "conjunctiveTags");
            this.validateNonNull(this.disjunctiveTags, "disjunctiveTags");
            this.validateNonNull(this.minFilters, "minFilters");
            this.validateNonNull(this.maxFilters, "maxFilters");
            this.validateNonNull(this.preferences, "preferences");
        }

        public Input() {}
    }

    public static class Output {
        public String status = "success";
        public Collection<SearchResult> results;
        public Output() {}
    }

    private final SearcherManager manager;

    public SearchService(final SearcherManager manager) {
        this.manager = manager;
    }

    @Override
    public Class<Input> getInputClass() {
        return Input.class;
    }

    @Override
    public Output handle(final Input input) throws ServiceFailure {
        final Searcher searcher = this.manager.getSearcher(input.shardId);
        if(searcher == null) {
            throw new ServiceFailure("shard not indexed, or has no data");
        }

        final long start = System.nanoTime();
        final Collection<SearchResult> results = searcher.search(
            input.conjunctiveTags,
            input.disjunctiveTags,
            input.minFilters,
            input.maxFilters,
            input.preferences,
            input.limit
        );
        System.out.println(System.nanoTime() - start);

        final Output output = new Output();
        output.results = results;
        return output;
    }

}