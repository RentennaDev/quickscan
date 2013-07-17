package co.deepthought.quickscan.service;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.Result;
import co.deepthought.quickscan.store.ResultStore;
import co.deepthought.quickscan.store.ResultTest;
import com.sleepycat.je.DatabaseException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class UpsertServiceTest {

    private UpsertService service;
    private ResultStore store;

    @Before
    public void setUp() throws DatabaseException {
        this.store = new ResultStore(":tmp");
        this.service = new UpsertService(this.store);
    }

    @Test
    public void testUpsert() throws ServiceFailure, DatabaseException {
        final UpsertService.Input input = new UpsertService.Input();
        input.result = ResultTest.mock()[3];
        this.service.handle(input);

        final Result result = this.store.getById("d");
        assertEquals("d", result.getId());
        assertTrue(result.hasTag("2"));
        assertTrue(result.hasTag("100"));
        assertTrue(result.hasTag("baby"));
        assertFalse(result.hasTag("jack"));
        assertEquals(2, result.getDocuments().size());
    }

}