package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.DocumentStore;
import co.deepthought.quickscan.store.DocumentTest;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;

import static junit.framework.Assert.assertEquals;

public class SearcherManagerTest {

    @Test
    public void testIndexing() throws SQLException {
        final DocumentStore store = new DocumentStore(":memory:");
        DocumentTest.mockDocuments(store);
        final SearcherManager manager = new SearcherManager(store);
        manager.index();
        final Searcher searcher = manager.getSearcher("a");
        SearcherManagerTest.assertIndexed(searcher);
    }

    public static void assertIndexed(final Searcher searcher) {
        final Collection<String> results = searcher.search(
            new ArrayList<String>(),
            new ArrayList<List<String>>(),
            new HashMap<String, Double>(),
            new HashMap<String, Double>(),
            new HashMap<String, Double>(),
            100);
        final Set<String> resultSet = new HashSet<String>(results);
        final Set<String> expected = new HashSet<String>();
        expected.add("a");
        expected.add("b");
        expected.add("c");
        assertEquals(expected, resultSet);
    }


}
