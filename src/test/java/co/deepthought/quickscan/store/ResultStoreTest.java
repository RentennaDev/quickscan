package co.deepthought.quickscan.store;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ResultStoreTest {

    private Result[] results;
    private ResultStore store;

    @Before
    public void setUp() throws DatabaseException {
        this.store = new ResultStore(":tmp");
        this.results = ResultTest.mock();
    }

    @Test
    public void testClean() throws DatabaseException {
        this.store.persist(this.results[0]);
        this.store.persist(this.results[1]);
        this.store.persist(this.results[2]);
        this.store.persist(this.results[3]);
        this.store.clean();
        assertNull(this.store.getById("a"));
        assertNull(this.store.getById("b"));
        assertNull(this.store.getById("c"));
        assertNull(this.store.getById("d"));
    }

    @Test
    public void testDeletebyId() throws DatabaseException {
        this.store.persist(this.results[0]);
        this.store.deleteById("a");
        assertNull(this.store.getById("a"));
    }

    @Test
    public void testGetByShardId() throws DatabaseException {
        this.store.persist(this.results[0]);
        this.store.persist(this.results[1]);
        this.store.persist(this.results[2]);
        this.store.persist(this.results[3]);
        final Result rb = new Result("twa", "b", null);
        this.store.persist(rb);

        final EntityCursor<Result> cursor = this.store.getByShardId("a");
        try {
            final Set<String> actual = new HashSet<>();
            for(final Result result : cursor) {
                actual.add(result.getId());
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
        this.store.persist(this.results[0]);
        this.store.persist(this.results[1]);
        this.store.persist(this.results[2]);
        this.store.persist(this.results[3]);
        final Result rb = new Result("twa", "b", null);
        this.store.persist(rb);
        final Set<String> expected = new HashSet<>();
        expected.add("a");
        expected.add("b");
        assertEquals(expected, this.store.getDistinctShardIds());
    }

    @Test
    public void testPersistence() throws DatabaseException {
        this.store.persist(this.results[0]);
        final Result r1 = this.store.getById("a");
        assertFalse(r1 == this.results[0]);
        assertEquals("a", r1.getId());
    }

    @Test
    public void testPersistenceNotFound() throws DatabaseException {
        final Result r2 = this.store.getById("?");
        assertNull(r2);
    }

    @Test
    public void testPersistenceSubobjects() throws DatabaseException {
        this.store.persist(this.results[3]);
        final Result result = this.store.getById("d");
        assertEquals(2, result.getDocuments().size());
        assertTrue(result.getDocumentById("41").hasTag("1"));
    }

}
