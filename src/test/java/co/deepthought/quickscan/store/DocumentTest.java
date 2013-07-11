package co.deepthought.quickscan.store;

import org.junit.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DocumentTest {

    private DocumentStore store;

    public static Document[] mockDocuments(final DocumentStore store) throws SQLException {
        final Document document1 = store.createDocument("a", "a", "a");

        final Document document2 = store.createDocument("b", "b", "a");
        document2.addTag("0");
        document2.addField("field-1", 10);
        document2.addField("field-2", 11);
        document2.addScore("score-3", 0.5);
        document2.addScore("score-1", 0.6);

        final Document document3 = store.createDocument("c", "c", "a");
        document3.addTag("1");
        document3.addTag("128");
        document3.addField("field-0", 100);
        document3.addScore("score-0", 0.4);
        document3.addScore("score-1", 0.8);

        final Document document4 = store.createDocument("d", "c", "a");
        document4.addTag("2");
        document4.addTag("100");
        document4.addTag("baby");
        document4.addField("field-FAKE", 100);
        document4.addScore("FAKE", 0.4);
        document4.addScore("score-1", 0.8);

        return new Document[] {document1, document2, document3, document4};
    }

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
        document.addScore("baby", 0.2);
        document.addScore("cats", 0.4);
        assertEquals(0.2, document.getScoreValue("baby"), 0);
        assertEquals(0.4, document.getScoreValue("cats"), 0);
        assertNull(document.getScoreValue("fragrance"));
        this.store.persistDocument(document);

        final Document queriedDocument = this.store.getDocumentById("A");
        assertEquals(0.2, queriedDocument.getScoreValue("baby"), 0);
        assertEquals(0.4, queriedDocument.getScoreValue("cats"), 0);
        assertNull(queriedDocument.getScoreValue("fragrance"));
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

    @Test
    public void testGetFieldValues() throws SQLException {
        final Document document = this.store.createDocument("A", "B", "C");
        document.addField("brat", 100);
        document.addField("met", 1001);
        final Map<String, Double> fieldValues = document.getFieldValues();
        assertEquals(2, fieldValues.size());
        assertEquals(100., fieldValues.get("brat"), 0);
        assertEquals(1001., fieldValues.get("met"), 0);
    }

    @Test
    public void testGetScoreValues() throws SQLException {
        final Document document = this.store.createDocument("A", "B", "C");
        document.addScore("brat", 0.1);
        document.addScore("met", 0.2);
        document.addScore("broof", 0.8);
        final Map<String, Double> values = document.getScoreValues();
        assertEquals(3, values.size());
        assertEquals(0.1, values.get("brat"), 0);
        assertEquals(0.2, values.get("met"), 0);
        assertEquals(0.8, values.get("broof"), 0);
    }

    @Test
    public void testGetTagNames() throws SQLException {
        final Document document = this.store.createDocument("A", "B", "C");
        document.addTag("baby");
        document.addTag("cats");
        final List<String> expected = Arrays.asList("baby", "cats");
        assertEquals(expected, document.getTagNames());
    }

}
