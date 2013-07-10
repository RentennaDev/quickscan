//package co.deepthought.quickscan;
//
//import org.junit.Test;
//
//import java.util.*;
//
//import static junit.framework.Assert.*;
//
//public class IndexTest {
//
//    @Test
//    public void testConjunctionOne() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAll("BIG");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(50, matching.size());
//    }
//
//    @Test
//    public void testConjunctionOneSingleMatch() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAll("LONELY:19");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(1, matching.size());
//    }
//
//    @Test
//    public void testConjunctionTwoDifferentPages() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAll("CROWDED:1");
//        query.filterTagsAll("LONELY:88");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(1, matching.size());
//    }
//
//    @Test
//    public void testConjunctionTwoNoMatches() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAll("BIG");
//        query.filterTagsAll("SMALL");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(0, matching.size());
//    }
//
//    @Test
//    public void testConjunctionMissing() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAll("BLANK");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(0, matching.size());
//    }
//
//    @Test
//    public void testConjunctionTwoSingleMatch() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAll("SMALL");
//        query.filterTagsAll("LONELY:19");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(1, matching.size());
//    }
//
//
//    @Test
//    public void testConjunctionTwoSomeMatches() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAll("SMALL");
//        query.filterTagsAll("CROWDED:45");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(4, matching.size());
//    }
//
//    @Test
//    public void testDisjunctionMissing() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAny("BLANK");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(0, matching.size());
//    }
//
//    @Test
//    public void testDisjunctionMissingAndPresent() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAny("BLANK", "LONELY:1");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(1, matching.size());
//    }
//
//    @Test
//    public void testDisjunctionMultiple() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAny("LONELY:1", "LONELY:2");
//        query.filterTagsAny("LONELY:2", "LONELY:3");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(1, matching.size());
//    }
//
//    @Test
//    public void testDisjunctionOne() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAny("SMALL");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(50, matching.size());
//    }
//
//    @Test
//    public void testDisjunctionTwo() {
//        final Index index = this.getLittleIndexOfTags();
//        final Query query = new Query();
//        query.filterTagsAny("SMALL", "LONELY:88");
//        final Collection<String> matching = index.scan(query, 1000);
//        assertEquals(51, matching.size());
//    }
//
//    @Test
//    public void testDuplicate() {
//        final List<Document> documents = new ArrayList<Document>();
//        documents.add(new Document("a"));
//        documents.add(new Document("a"));
//        final Index index = new Index(documents);
//        final Collection<String> matching = index.scan(new Query(), 10000);
//        assertEquals(1, matching.size());
//    }
//
//    @Test
//    public void testMaxOneField() {
//        final Index index = this.getLittleIndexOfFields();
//        final Query query = new Query();
//        query.filterFieldMax("a", 2);
//        final Collection<String> actual = index.scan(query, 1000);
//
//        final Collection<String> expected = new HashSet<String>();
//        expected.add("0");
//        expected.add("1");
//        expected.add("2");
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testMinOneField() {
//        final Index index = this.getLittleIndexOfFields();
//        final Query query = new Query();
//        query.filterFieldMin("b", 998);
//        final Collection<String> actual = index.scan(query, 1000);
//
//        final Collection<String> expected = new HashSet<String>();
//        expected.add("0");
//        expected.add("1");
//        expected.add("2");
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testRangeOneField() {
//        final Index index = this.getLittleIndexOfFields();
//        final Query query = new Query();
//        query.filterFieldRange("a", 10, 12);
//        final Collection<String> actual = index.scan(query, 1000);
//
//        final Collection<String> expected = new HashSet<String>();
//        expected.add("10");
//        expected.add("11");
//        expected.add("12");
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testMaxTwoField() {
//        final Index index = this.getLittleIndexOfFields();
//        final Query query = new Query();
//        query.filterFieldMax("a", 501);
//        query.filterFieldMax("b", 501);
//        final Collection<String> actual = index.scan(query, 1000);
//
//        final Collection<String> expected = new HashSet<String>();
//        expected.add("499");
//        expected.add("500");
//        expected.add("501");
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testMinTwoField() {
//        final Index index = this.getLittleIndexOfFields();
//        final Query query = new Query();
//        query.filterFieldMin("a", 499);
//        query.filterFieldMin("b", 499);
//        final Collection<String> actual = index.scan(query, 1000);
//
//        final Collection<String> expected = new HashSet<String>();
//        expected.add("499");
//        expected.add("500");
//        expected.add("501");
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testMultiPage() {
//        final List<Document> documents = new ArrayList<Document>();
//        for(int i = 0; i < 8000; i++) {
//            final Document document = new Document(Integer.toString(i));
//            document.addField("a", i);
//            documents.add(document);
//        }
//        final Index bigIndex = new Index(documents);
//        final Query query = new Query();
//        query.filterFieldMax("a", 6000);
//        final Collection<String> matching = bigIndex.scan(query, 10000);
//        assertEquals(6001, matching.size());
//    }
//
//    @Test
//    public void testSortingSingleField() {
//        final List<Document> documents = new ArrayList<Document>();
//        Document document = new Document("a");
//        document.addScore("score", 0.1, 1);
//        documents.add(document);
//        document = new Document("b");
//        document.addScore("score", 0.9, 1);
//        documents.add(document);
//        document = new Document("c");
//        document.addScore("score", 0.5, 1);
//        documents.add(document);
//        final Index index = new Index(documents);
//        final Object[] results = index.scan(new Query(), 10).toArray();
//        assertEquals("b", results[0]);
//        assertEquals("c", results[1]);
//        assertEquals("a", results[2]);
//    }
//
//    @Test
//    public void testSortingMultiField() {
//        final List<Document> documents = new ArrayList<Document>();
//        Document document = new Document("a");
//        document.addScore("score", 0.4, 1);
//        document.addScore("score2", 0.4, 1); // 0.4
//        documents.add(document);
//        document = new Document("b");
//        document.addScore("score", 0.1, 1);
//        document.addScore("score2", 0.5, 1); // 0.3
//        documents.add(document);
//        document = new Document("c");
//        document.addScore("score", 0.6, 1);
//        document.addScore("score2", 0.3, 1); // 0.45
//        documents.add(document);
//        final Index index = new Index(documents);
//        final Object[] results = index.scan(new Query(), 10).toArray();
//        assertEquals("c", results[0]);
//        assertEquals("a", results[1]);
//        assertEquals("b", results[2]);
//    }
//
//    @Test
//    public void testSortingMultiFieldWithConfidences() {
//        final List<Document> documents = new ArrayList<Document>();
//        Document document = new Document("a");
//        document.addScore("score", 0.9, 1);
//        document.addScore("score2", 0.1, 0.1);
//        documents.add(document);
//        document = new Document("b");
//        document.addScore("score", 0.5, 1);
//        document.addScore("score2", 0.5, 0.1);
//        documents.add(document);
//        document = new Document("c");
//        document.addScore("score", 0.1, 1);
//        document.addScore("score2", 0.9, 0.1);
//        documents.add(document);
//        final Index index = new Index(documents);
//        final Object[] results = index.scan(new Query(), 10).toArray();
//        assertEquals("a", results[0]);
//        assertEquals("b", results[1]);
//        assertEquals("c", results[2]);
//    }
//
//    @Test
//    public void testSortingMultiFieldWithPreferences() {
//        final List<Document> documents = new ArrayList<Document>();
//        Document document = new Document("a");
//        document.addScore("score", 0.9, 1);
//        document.addScore("score2", 0.1, 1);
//        documents.add(document);
//        document = new Document("b");
//        document.addScore("score", 0.5, 1);
//        document.addScore("score2", 0.5, 1);
//        documents.add(document);
//        document = new Document("c");
//        document.addScore("score", 0.1, 1);
//        document.addScore("score2", 0.9, 1);
//        documents.add(document);
//        final Index index = new Index(documents);
//        final Query query = new Query();
//        query.setPreference("score", 0.1);
//        query.setPreference("score2", 1);
//        final Object[] results = index.scan(query, 10).toArray();
//        assertEquals("c", results[0]);
//        assertEquals("b", results[1]);
//        assertEquals("a", results[2]);
//    }
//
//    private Index getLittleIndexOfFields() {
//        final List<Document> documents = new ArrayList<Document>();
//        for(int i = 0; i < 1000; i++) {
//            final Document document = new Document(Integer.toString(i));
//            document.addField("a", i);
//            document.addField("b", 1000-i);
//            documents.add(document);
//        }
//        return new Index(documents);
//    }
//
//    private Index getLittleIndexOfTags() {
//        final List<Document> documents = new ArrayList<Document>();
//        for(int i = 0; i < 100; i++) {
//            final Document document = new Document(Integer.toString(i));
//            document.addTag("ALL");
//            document.addTag("LONELY:" + i);
//            for(int j = 0; j < i; j++) {
//                document.addTag("CROWDED:" + j);
//            }
//            document.addTag(i < 50 ? "SMALL" : "BIG");
//            documents.add(document);
//        }
//        return new Index(documents);
//    }
//
//}
