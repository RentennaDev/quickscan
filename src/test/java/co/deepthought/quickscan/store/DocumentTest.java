package co.deepthought.quickscan.store;

import org.junit.*;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class DocumentTest {

    private DocumentStore store;

    @Before
    public void setUp() throws SQLException {
        this.store = new DocumentStore(":memory:");
    }

    @Test
    public void testAddField() throws SQLException {
        final Document document = this.store.createDocument("A", "B", "C");
        document.addField("baby", 0.2);
        document.addField("cats", 0.4);
        assertEquals(0.2, document.getFieldValue("baby"), 0);
        assertEquals(0.4, document.getFieldValue("cats"), 0);
        assertNull(document.getFieldValue("fragrance"));
        this.store.persistDocument(document);

        final Document queriedDocument = this.store.getDocumentById("A");
        assertEquals(0.2, queriedDocument.getFieldValue("baby"), 0);
        assertEquals(0.4, queriedDocument.getFieldValue("cats"), 0);
        assertNull(queriedDocument.getFieldValue("fragrance"));
    }

    @Test
    public void testAddScore() throws SQLException {
        final Document document = this.store.createDocument("A", "B", "C");
        document.addScore("baby", Score.Valence.NEUTRAL, 0.2);
        document.addScore("cats", Score.Valence.NEUTRAL, 0.4);
        assertEquals(0.2, document.getScoreValue("baby", Score.Valence.NEUTRAL), 0);
        assertEquals(0.4, document.getScoreValue("cats", Score.Valence.NEUTRAL), 0);
        assertNull(document.getScoreValue("fragrance", Score.Valence.NEUTRAL));
        assertNull(document.getScoreValue("baby", Score.Valence.POSITIVE));
        this.store.persistDocument(document);

        final Document queriedDocument = this.store.getDocumentById("A");
        assertEquals(0.2, queriedDocument.getScoreValue("baby", Score.Valence.NEUTRAL), 0);
        assertEquals(0.4, queriedDocument.getScoreValue("cats", Score.Valence.NEUTRAL), 0);
        assertNull(queriedDocument.getScoreValue("fragrance", Score.Valence.NEUTRAL));
        assertNull(queriedDocument.getScoreValue("baby", Score.Valence.POSITIVE));
    }

    @Test
    public void testAddTags() throws SQLException {
        final Document document = this.store.createDocument("A", "B", "C");
        document.addTag("baby");
        document.addTag("cats");
        assertTrue(document.hasTag("baby"));
        assertTrue(document.hasTag("cats"));
        assertFalse(document.hasTag("fragrance"));
        this.store.persistDocument(document);

        final Document queriedDocument = this.store.getDocumentById("A");
        assertTrue(queriedDocument.hasTag("baby"));
        assertTrue(queriedDocument.hasTag("cats"));
        assertFalse(queriedDocument.hasTag("fragrance"));
    }

}
