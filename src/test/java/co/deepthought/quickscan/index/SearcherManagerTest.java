package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.ResultStore;
import co.deepthought.quickscan.store.Result;
import co.deepthought.quickscan.store.ResultTest;
import com.sleepycat.je.DatabaseException;
import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;

public class SearcherManagerTest {

    @Test
    public void testIndexing() throws DatabaseException {
        final ResultStore store = new ResultStore(":tmp");
        for(final Result result : ResultTest.mock()) {
            store.persist(result);
        }
        final SearcherManager manager = new SearcherManager(store);
        manager.index();
        final Searcher searcher = manager.getSearcher("a");
        SearcherManagerTest.assertIndexed(searcher);
    }

    public static void assertIndexed(final Searcher searcher) throws DatabaseException {
        final PaginatedResults<SearchResult> results = searcher.search(
            new ArrayList<String>(),
            new ArrayList<List<String>>(),
            new HashMap<String, Double>(),
            new HashMap<String, Double>(),
            new HashMap<String, Double>(),
            100,
            0);
        final Set<SearchResult> resultSet = new HashSet<>(results.getResults());
        final Set<SearchResult> expected = new HashSet<>();
        expected.add(new SearchResult("a", null, null));
        expected.add(new SearchResult("b", null, null));
        expected.add(new SearchResult("c", null, null));
        expected.add(new SearchResult("d", null, null)); // equality only counts resultIds
        assertEquals(expected, resultSet);
    }

}