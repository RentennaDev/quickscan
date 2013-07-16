package co.deepthought.quickscan.service;

import co.deepthought.quickscan.service.ServiceFailure;
import co.deepthought.quickscan.service.UpsertService;
import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;
import co.deepthought.quickscan.store.Score;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class UpsertServiceTest {

    private UpsertService service;
    private DocumentStore store;

    @Before
    public void setUp() throws SQLException {
        this.store = new DocumentStore(":memory:");
        this.service = new UpsertService(this.store);
    }

    @Test
    public void testUpsertNew() throws ServiceFailure, SQLException {
        final UpsertService.Input input = new UpsertService.Input();
        input.documentId = "AAA";
        input.shardId = "BBB";
        input.resultId = "CCC";
        input.tags = new String[] {"a", "b", "c"};
        input.fields = new HashMap<String, Double>();
        input.fields.put("A", 1.1);
        input.scores = new HashMap<String, UpsertService.Input.Score>();
        final UpsertService.Input.Score score = new UpsertService.Input.Score();
        score.value = 0.5;
        input.scores.put("B", score);
        this.service.handle(input);

        final Document document = this.store.getDocumentById("AAA");
        assertEquals("AAA", document.getDocumentId());
        assertEquals("BBB", document.getShardId());
        assertEquals("CCC", document.getResultId());
        assertTrue(document.hasTag("a"));
        assertTrue(document.hasTag("b"));
        assertTrue(document.hasTag("c"));
        assertFalse(document.hasTag("d"));
        assertEquals(1.1, document.getFieldValue("A"));
        assertEquals(0.5, document.getScoreValue("B"));
    }

    @Test
    public void testUpsertReplace() throws ServiceFailure, SQLException {
        final UpsertService.Input input = new UpsertService.Input();
        input.documentId = "AAA";
        input.shardId = "BBB";
        input.resultId = "CCC";
        input.tags = new String[] {"a", "b", "c"};
        input.fields = new HashMap<String, Double>();
        input.scores = new HashMap<String, UpsertService.Input.Score>();
        this.service.handle(input);

        final UpsertService.Input input2 = new UpsertService.Input();
        input2.documentId = "AAA";
        input2.shardId = "BBB";
        input2.resultId = "CCC";
        input2.tags = new String[] {"a"};
        input2.fields = new HashMap<String, Double>();
        input2.scores = new HashMap<String, UpsertService.Input.Score>();
        this.service.handle(input2);

        final Document document = this.store.getDocumentById("AAA");
        assertEquals("AAA", document.getDocumentId());
        assertEquals("BBB", document.getShardId());
        assertEquals("CCC", document.getResultId());
        assertTrue(document.hasTag("a"));
        assertFalse(document.hasTag("b"));
        assertFalse(document.hasTag("c"));
    }

}