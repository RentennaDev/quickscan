package co.deepthought.quickscan.store;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DocumentStoreTest {

    private Document[] documents;
    private DocumentStore store;

    @Before
    public void setUp() throws DatabaseException {
        this.store = new DocumentStore(":tmp");
        this.documents = DocumentTest.mock();
    }

    @Test
    public void testClean() throws DatabaseException {
        this.store.persist(this.documents[0]);
        this.store.persist(this.documents[1]);
        this.store.persist(this.documents[2]);
        this.store.persist(this.documents[3]);
        this.store.clean();
        assertNull(this.store.getById("a"));
        assertNull(this.store.getById("b"));
        assertNull(this.store.getById("c"));
        assertNull(this.store.getById("d"));
    }

    @Test
    public void testGetByShardId() throws DatabaseException {
        this.store.persist(this.documents[0]);
        this.store.persist(this.documents[1]);
        this.store.persist(this.documents[2]);
        this.store.persist(this.documents[3]);
        final Document rb = new Document("twa", "b", null);
        this.store.persist(rb);

        final EntityCursor<Document> cursor = this.store.getByShardId("a");
        try {
            final Set<String> actual = new HashSet<>();
            for(final Document document : cursor) {
                actual.add(document.getId());
            }

            final Set<String> expected = new HashSet<>();
            expected.add("a");
            expected.add("b");
            expected.add("c");
            expected.add("d");
            assertEquals(expected, actual);
        }
        finally {
            cursor.close();
        }
    }

    @Test
    public void testGetDistinctShardIds() throws DatabaseException {
        this.store.persist(this.documents[0]);
        this.store.persist(this.documents[1]);
        this.store.persist(this.documents[2]);
        this.store.persist(this.documents[3]);
        final Document rb = new Document("twa", "b", null);
        this.store.persist(rb);
        final Set<String> expected = new HashSet<>();
        expected.add("a");
        expected.add("b");
        assertEquals(expected, this.store.getDistinctShardIds());
    }

    @Test
    public void testPersistence() throws DatabaseException {
        this.store.persist(this.documents[0]);
        final Document r1 = this.store.getById("a");
        assertFalse(r1 == this.documents[0]);
        assertEquals("a", r1.getId());
    }

    @Test
    public void testPersistenceNotFound() throws DatabaseException {
        final Document r2 = this.store.getById("?");
        assertNull(r2);
    }

}
