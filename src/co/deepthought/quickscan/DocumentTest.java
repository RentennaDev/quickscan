package co.deepthought.quickscan;

import org.junit.Test;

import static org.junit.Assert.*;

public class DocumentTest {

    @Test
    public void testIdentifier() {
        final Document document = new Document("abc");
        assertEquals("abc", document.identifier);
    }

    @Test
    public void testFields() {
        final Document document = new Document("abc");
        document.addField("fran", 100);
        document.addField("drescher", 21);
        assertEquals(2, document.fields.size());
        assertEquals(100, (int) document.fields.get("fran"));
        assertEquals(21, (int) document.fields.get("drescher"));
    }

    @Test
    public void testTags() {
        final Document document = new Document("abc");
        document.addTag("cat", "hat");
        assertEquals(2, document.tags.size());
        assertTrue("cat", document.tags.contains("cat"));
        assertTrue("hat", document.tags.contains("hat"));
    }

    @Test
    public void testScores() {
        final Document document = new Document("abc");
        document.addScore("fran", 0.5, 0.8);
        document.addScore("drescher", 0.2, 0.3);
        assertEquals(2, document.scores.size());
        assertEquals(2, document.scoreConfidences.size());
        assertEquals(0.5, document.scores.get("fran"), 0.);
        assertEquals(0.8, document.scoreConfidences.get("fran"), 0.);
        assertEquals(0.2, document.scores.get("drescher"), 0.);
        assertEquals(0.3, document.scoreConfidences.get("drescher"), 0.);
    }

}
