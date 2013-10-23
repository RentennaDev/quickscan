package co.deepthought.quickscan.store;

import com.google.gson.Gson;
import org.junit.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DocumentTest {

    public static Document[] mock() {
        final Document r1 = new Document("a", "a", null); // 0

        final Document r2 = new Document("b", "a", null); // 1
        r2.addTag("0");
        r2.addField("field-1", 10);
        r2.addField("field-2", 11);
        r2.addScore("score-3", false, 15);
        r2.addScore("score-1", false, 6);

        final Document r3 = new Document("c", "a", null); // 3
        r3.addTag("1");
        r3.addTag("128");
        r3.addField("field-0", 100);
        r3.addScore("score-0", false, 2);
        r3.addScore("score-1", false, 8);

        final Document r4 = new Document("d", "a", null); // 5
        r4.addTag("2");
        r4.addTag("100");
        r4.addTag("baby");
        r4.addField("field-FAKE", 100);
        r4.addScore("FAKE", false, 0.4);
        r4.addScore("score-1", false, 8);

        return new Document[] {r1, r2, r3, r4};
    }

    private Document[] documents;

    @Before
    public void setUp() {
        this.documents = DocumentTest.mock();
    }

    @Test
    public void testAddField() {
        final Document document = this.documents[0];
        document.addField("baby", 0.2);
        document.addField("cats", 0.4);
        assertEquals(0.2, document.getFieldValue("baby"), 0);
        assertEquals(0.4, document.getFieldValue("cats"), 0);
        assertNull(document.getFieldValue("fragrance"));
    }

    @Test
    public void testAddScore() {
        final Document document = this.documents[0];
        document.addScore("baby", false, 0.2);
        document.addScore("cats", false, 0.4);
        assertEquals(0.2, document.getScoreValue("baby"), 0);
        assertEquals(0.4, document.getScoreValue("cats"), 0);
        assertNull(document.getScoreValue("fragrance"));
    }

    @Test
    public void testAddTags() {
        final Document document = this.documents[0];
        document.addTag("baby");
        document.addTag("cats");
        assertTrue(document.hasTag("baby"));
        assertTrue(document.hasTag("cats"));
        assertFalse(document.hasTag("fragrance"));
    }

    @Test
    public void testGetFieldValues() {
        final Document document = this.documents[1];
        final Map<String, Double> fieldValues = document.getFieldValues();
        assertEquals(2, fieldValues.size());
        assertEquals(10, fieldValues.get("field-1"), 0);
        assertEquals(11., fieldValues.get("field-2"), 0);
    }

    @Test
    public void testGetScoreValues() {
        final Document document = this.documents[1];
        final Map<String, Double> values = document.getScoreValues();
        assertEquals(2, values.size());
        assertEquals(6, values.get("score-1"), 0);
        assertEquals(15, values.get("score-3"), 0);
    }

    @Test
    public void testGetTagNames() {
        final Document document = this.documents[2];
        final List<String> expected = Arrays.asList("1", "128");
        assertEquals(expected, document.getTagNames());
    }

    @Test
    public void testGson() {
        final Document document = this.documents[3];
        final Gson gson = new Gson();
        final String serialized = gson.toJson(document);
        final Document deserialized = gson.fromJson(serialized, Document.class);
        assertEquals("d", deserialized.getId());
    }

}