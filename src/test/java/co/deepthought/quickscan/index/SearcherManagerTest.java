package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.DocumentStore;
import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentTest;
import com.sleepycat.je.DatabaseException;
import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;

public class SearcherManagerTest {

    @Test
    public void testIndexing() throws DatabaseException {
        final DocumentStore store = new DocumentStore(":tmp");
        for(final Document document : DocumentTest.mock()) {
            store.persist(document);
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
        // a is missing because it has no scores to use for ranking!
        expected.add(new SearchResult("b", null, null));
        expected.add(new SearchResult("c", null, null));
        expected.add(new SearchResult("d", null, null));
        assertEquals(expected, resultSet);
    }

}