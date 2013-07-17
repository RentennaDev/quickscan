package co.deepthought.quickscan.store;

import com.google.gson.Gson;
import org.junit.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ResultTest {

    public static Result[] mock() {
        final Result r1 = new Result("a", "a", null); // 0


        final Result r2 = new Result("b", "a", null); // 1
        r2.addTag("0");
        r2.addField("field-1", 10);
        r2.addField("field-2", 11);
        r2.addScore("score-3", false, 15);
        r2.addScore("score-1", false, 6);

        final Document d21 = r2.createDocument("21"); // 2
        d21.addTag("21");
        d21.addTag("1");


        final Result r3 = new Result("c", "a", null); // 3
        r3.addTag("1");
        r3.addTag("128");
        r3.addField("field-0", 100);
        r3.addScore("score-0", false, 2);
        r3.addScore("score-1", false, 8);

        final Document d31 = r3.createDocument("31"); // 4
        d31.addTag("1");
        d31.addScore("score-3", true, 7.5);


        final Result r4 = new Result("d", "a", null); // 5
        r4.addTag("2");
        r4.addTag("100");
        r4.addTag("baby");
        r4.addField("field-FAKE", 100);
        r4.addScore("FAKE", false, 0.4);
        r4.addScore("score-1", false, 8);

        final Document d41 = r4.createDocument("41"); // 6
        d41.addTag("1");
        d41.addScore("score-3", true, 22.5);
        d41.addField("field-2", 12);

        final Document d42 = r4.createDocument("42"); // 7
        d42.addTag("1");
        d42.addScore("score-3", true, 15);

        return new Result[] {r1, r2, r3, r4};
    }

    private Result[] results;

    @Before
    public void setUp() {
        this.results = ResultTest.mock();
    }

    @Test
    public void testCreateDocument() {
        final Result result = this.results[0];
        final Document d1 = result.createDocument("a");
        final Document d2 = result.createDocument("b");
        assertEquals(2, result.getDocuments().size());
        assertTrue(result.getDocuments().contains(d1));
        assertTrue(result.getDocuments().contains(d2));
    }

    @Test
    public void testAddField() {
        final Result result = this.results[0];
        result.addField("baby", 0.2);
        result.addField("cats", 0.4);
        assertEquals(0.2, result.getFieldValue("baby"), 0);
        assertEquals(0.4, result.getFieldValue("cats"), 0);
        assertNull(result.getFieldValue("fragrance"));
    }

    @Test
    public void testAddScore() {
        final Result result = this.results[0];
        result.addScore("baby", false, 0.2);
        result.addScore("cats", false, 0.4);
        assertEquals(0.2, result.getScoreValue("baby"), 0);
        assertEquals(0.4, result.getScoreValue("cats"), 0);
        assertNull(result.getScoreValue("fragrance"));
    }

    @Test
    public void testAddTags() {
        final Result result = this.results[0];
        result.addTag("baby");
        result.addTag("cats");
        assertTrue(result.hasTag("baby"));
        assertTrue(result.hasTag("cats"));
        assertFalse(result.hasTag("fragrance"));
    }

    @Test
    public void testGetAllFields() {
        assertEquals(0, this.results[0].getAllFields().size());
        assertEquals(2, this.results[1].getAllFields().size());
        assertEquals(1, this.results[2].getAllFields().size());
        assertEquals(2, this.results[3].getAllFields().size());
    }

    @Test
    public void testGetAllScores() {
        assertEquals(0, this.results[0].getAllScores().size());
        assertEquals(2, this.results[1].getAllScores().size());
        assertEquals(3, this.results[2].getAllScores().size());
        assertEquals(4, this.results[3].getAllScores().size());
    }

    @Test
    public void testGetAllTags() {
        assertEquals(0, this.results[0].getAllTags().size());
        assertEquals(3, this.results[1].getAllTags().size());
        assertEquals(3, this.results[2].getAllTags().size());
        assertEquals(5, this.results[3].getAllTags().size());
    }

    @Test
    public void testGetFieldValues() {
        final Result result = this.results[1];
        final Map<String, Double> fieldValues = result.getFieldValues();
        assertEquals(2, fieldValues.size());
        assertEquals(10, fieldValues.get("field-1"), 0);
        assertEquals(11., fieldValues.get("field-2"), 0);
    }

    @Test
    public void testGetScoreValues() {
        final Result result = this.results[1];
        final Map<String, Double> values = result.getScoreValues();
        assertEquals(2, values.size());
        assertEquals(6, values.get("score-1"), 0);
        assertEquals(15, values.get("score-3"), 0);
    }

    @Test
    public void testGetTagNames() {
        final Result result = this.results[2];
        final List<String> expected = Arrays.asList("1", "128");
        assertEquals(expected, result.getTagNames());
    }

    @Test
    public void testGson() {
        final Result result = this.results[3];
        final Gson gson = new Gson();
        final String serialized = gson.toJson(result);
        final Result deserialized = gson.fromJson(serialized, Result.class);
        assertEquals("d", deserialized.getId());
        assertEquals(2, deserialized.getDocuments().size());
        for(final Document document : deserialized.getDocuments()) {
            assertTrue(document.hasTag("1"));
        }
    }

}