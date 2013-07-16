package co.deepthought.quickscan.store;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DocumentStoreTest {

    private DocumentStore store;

    @Before
    public void setUp() throws SQLException {
        this.store = new DocumentStore(":memory:");
    }

    @Test
    public void testDeleteById() throws SQLException {
        final Document document1 = this.store.createDocument("A", "B", "C");
        document1.addTag("a");
        this.store.persistDocument(document1);

        final Document document2 = this.store.createDocument("E", "B", "C");
        document2.addTag("b");
        this.store.persistDocument(document2);

        this.store.deleteById("A");
        assertNull(this.store.getDocumentById("A"));
        assertNotNull(this.store.getDocumentById("E"));

        final Set<String> expectedTags = new HashSet<String>();
        expectedTags.add("b");
//        assertEquals(expectedTags, this.store.getDistinctTags("C"));
    }

    @Test
    public void testGetDistinctShards() throws SQLException {
        DocumentTest.mockDocuments(this.store);
        final Set<String> expected = new HashSet<String>();
        expected.add("a");
        assertEquals(expected, this.store.getDistinctShards());
        this.store.createDocument("e", "e", "c");
        expected.add("c");
        assertEquals(expected, this.store.getDistinctShards());
    }

}
