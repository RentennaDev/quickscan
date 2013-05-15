package co.deepthought.quickscan;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;

public class IndexTest {

    @Test
    public void testConjunctionOne() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAll("BIG");
        final Set<String> matching = index.scan(query);
        assertEquals(50, matching.size());
    }

    @Test
    public void testConjunctionOneSingleMatch() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAll("LONELY:19");
        final Set<String> matching = index.scan(query);
        assertEquals(1, matching.size());
    }

    @Test
    public void testConjunctionTwoDifferentPages() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAll("CROWDED:1");
        query.filterTagsAll("LONELY:88");
        final Set<String> matching = index.scan(query);
        assertEquals(1, matching.size());
    }

    @Test
    public void testConjunctionTwoNoMatches() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAll("BIG");
        query.filterTagsAll("SMALL");
        final Set<String> matching = index.scan(query);
        assertEquals(0, matching.size());
    }

    @Test
    public void testConjunctionMissing() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAll("BLANK");
        final Set<String> matching = index.scan(query);
        assertEquals(0, matching.size());
    }

    @Test
    public void testConjunctionTwoSingleMatch() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAll("SMALL");
        query.filterTagsAll("LONELY:19");
        final Set<String> matching = index.scan(query);
        assertEquals(1, matching.size());
    }


    @Test
    public void testConjunctionTwoSomeMatches() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAll("SMALL");
        query.filterTagsAll("CROWDED:45");
        final Set<String> matching = index.scan(query);
        assertEquals(4, matching.size());
    }

    @Test
    public void testDisjunctionMissing() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAny("BLANK");
        final Set<String> matching = index.scan(query);
        assertEquals(0, matching.size());
    }

    @Test
    public void testDisjunctionMissingAndPresent() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAny("BLANK", "LONELY:1");
        final Set<String> matching = index.scan(query);
        assertEquals(1, matching.size());
    }

    @Test
    public void testDisjunctionMultiple() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAny("LONELY:1", "LONELY:2");
        query.filterTagsAny("LONELY:2", "LONELY:3");
        final Set<String> matching = index.scan(query);
        assertEquals(1, matching.size());
    }

    @Test
    public void testDisjunctionOne() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAny("SMALL");
        final Set<String> matching = index.scan(query);
        assertEquals(50, matching.size());
    }

    @Test
    public void testDisjunctionTwo() {
        final Index index = this.getLittleIndexOfTags();
        final Query query = new Query();
        query.filterTagsAny("SMALL", "LONELY:88");
        final Set<String> matching = index.scan(query);
        assertEquals(51, matching.size());
    }

    @Test
    public void testMaxOneField() {
        final Index index = this.getLittleIndexOfFields();
        final Query query = new Query();
        query.filterFieldMax("a", 2);
        final Set<String> actual = index.scan(query);

        final Set<String> expected = new HashSet<String>();
        expected.add("0");
        expected.add("1");
        expected.add("2");

        assertEquals(expected, actual);
    }

    @Test
    public void testMinOneField() {
        final Index index = this.getLittleIndexOfFields();
        final Query query = new Query();
        query.filterFieldMin("b", 998);
        final Set<String> actual = index.scan(query);

        final Set<String> expected = new HashSet<String>();
        expected.add("0");
        expected.add("1");
        expected.add("2");

        assertEquals(expected, actual);
    }

    @Test
    public void testRangeOneField() {
        final Index index = this.getLittleIndexOfFields();
        final Query query = new Query();
        query.filterFieldRange("a", 10, 12);
        final Set<String> actual = index.scan(query);

        final Set<String> expected = new HashSet<String>();
        expected.add("10");
        expected.add("11");
        expected.add("12");

        assertEquals(expected, actual);
    }

    @Test
    public void testMaxTwoField() {
        final Index index = this.getLittleIndexOfFields();
        final Query query = new Query();
        query.filterFieldMax("a", 501);
        query.filterFieldMax("b", 501);
        final Set<String> actual = index.scan(query);

        final Set<String> expected = new HashSet<String>();
        expected.add("499");
        expected.add("500");
        expected.add("501");

        assertEquals(expected, actual);
    }

    @Test
    public void testMinTwoField() {
        final Index index = this.getLittleIndexOfFields();
        final Query query = new Query();
        query.filterFieldMin("a", 499);
        query.filterFieldMin("b", 499);
        final Set<String> actual = index.scan(query);

        final Set<String> expected = new HashSet<String>();
        expected.add("499");
        expected.add("500");
        expected.add("501");

        assertEquals(expected, actual);
    }

    @Test
    public void testMultiPage() {
        final List<Document> documents = new ArrayList<Document>();
        for(int i = 0; i < 8000; i++) {
            final Document document = new Document(Integer.toString(i));
            document.addField("a", i);
            documents.add(document);
        }
        final Index bigIndex = new Index(documents);
        final Query query = new Query();
        query.filterFieldMax("a", 6000);
        final Set<String> matching = bigIndex.scan(query);
        assertEquals(6001, matching.size());
    }

    private Index getLittleIndexOfFields() {
        final List<Document> documents = new ArrayList<Document>();
        for(int i = 0; i < 1000; i++) {
            final Document document = new Document(Integer.toString(i));
            document.addField("a", i);
            document.addField("b", 1000-i);
            documents.add(document);
        }
        return new Index(documents);
    }

    private Index getLittleIndexOfTags() {
        final List<Document> documents = new ArrayList<Document>();
        for(int i = 0; i < 100; i++) {
            final Document document = new Document(Integer.toString(i));
            document.addTag("ALL");
            document.addTag("LONELY:" + i);
            for(int j = 0; j < i; j++) {
                document.addTag("CROWDED:" + j);
            }
            document.addTag(i < 50 ? "SMALL" : "BIG");
            documents.add(document);
        }
        return new Index(documents);
    }

}
