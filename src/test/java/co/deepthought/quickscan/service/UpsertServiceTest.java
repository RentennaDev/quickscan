package co.deepthought.quickscan.service;

import co.deepthought.quickscan.server.ServiceFailure;
import co.deepthought.quickscan.server.UpsertService;
import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;
import co.deepthought.quickscan.store.DocumentTest;
import com.sleepycat.je.DatabaseException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class UpsertServiceTest {

    private UpsertService service;
    private DocumentStore store;

    @Before
    public void setUp() throws DatabaseException {
        this.store = new DocumentStore(":tmp");
        this.service = new UpsertService(this.store);
    }

    @Test
    public void testUpsert() throws ServiceFailure, DatabaseException {
        final UpsertService.Input input = new UpsertService.Input();
        input.document = DocumentTest.mock()[3];
        this.service.handle(input);

        final Document document = this.store.getById("d");
        assertEquals("d", document.getId());
        assertTrue(document.hasTag("2"));
        assertTrue(document.hasTag("100"));
        assertTrue(document.hasTag("baby"));
        assertFalse(document.hasTag("jack"));
    }

}