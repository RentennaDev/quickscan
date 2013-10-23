package co.deepthought.quickscan.service;

import co.deepthought.quickscan.index.SearcherManager;
import co.deepthought.quickscan.server.IndexService;
import co.deepthought.quickscan.server.ServiceFailure;
import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;
import co.deepthought.quickscan.store.DocumentTest;
import com.sleepycat.je.DatabaseException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class IndexServiceTest {

    private SearcherManager manager;
    private IndexService service;

    @Before
    public void setUp() throws DatabaseException {
        final DocumentStore store = new DocumentStore(":tmp");
        for(final Document document : DocumentTest.mock()) {
            store.persist(document);
        }
        store.persist(new Document("e", "e", null));
        this.manager = new SearcherManager(store);
        this.service = new IndexService(this.manager);
    }

    @Test
    public void testIndexing() throws DatabaseException, ServiceFailure {
        final IndexService.Input input = new IndexService.Input();
        this.service.handle(input);
        assertNotNull(this.manager.getSearcher("a"));
        assertNotNull(this.manager.getSearcher("e"));
    }

    @Test
    public void testIndexingForShard() throws DatabaseException, ServiceFailure {
        final IndexService.Input input = new IndexService.Input();
        input.shardId = "e";
        this.service.handle(input);
        assertNull(this.manager.getSearcher("a"));
        assertNotNull(this.manager.getSearcher("e"));
    }

}
