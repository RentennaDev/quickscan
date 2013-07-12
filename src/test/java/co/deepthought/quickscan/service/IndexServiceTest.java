package co.deepthought.quickscan.service;

import co.deepthought.quickscan.index.SearcherManager;
import co.deepthought.quickscan.store.DocumentStore;
import co.deepthought.quickscan.store.DocumentTest;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class IndexServiceTest {

    private SearcherManager manager;
    private IndexService service;

    @Before
    public void setUp() throws SQLException {
        final DocumentStore store = new DocumentStore(":memory:");
        DocumentTest.mockDocuments(store);
        store.createDocument("e", "e", "e");
        this.manager = new SearcherManager(store);
        this.service = new IndexService(this.manager);
    }

    @Test
    public void testIndexing() throws SQLException, ServiceFailure {
        final IndexService.Input input = new IndexService.Input();
        this.service.handle(input);
        assertNotNull(this.manager.getSearcher("a"));
        assertNotNull(this.manager.getSearcher("e"));
    }

    @Test
    public void testIndexingForShard() throws SQLException, ServiceFailure {
        final IndexService.Input input = new IndexService.Input();
        input.shardId = "e";
        this.service.handle(input);
        assertNull(this.manager.getSearcher("a"));
        assertNotNull(this.manager.getSearcher("e"));
    }

}
