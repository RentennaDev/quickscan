package co.deepthought.quickscan;

import org.junit.Test;

import static junit.framework.Assert.*;

public class QueryTest {

    @Test
    public void testFieldMax() {
        final Query query = new Query();
        query.filterFieldMax("cat", 1);
        assertEquals(1, (int) query.fieldMaxs.get("cat"));
        query.filterFieldMax("cat", 2);
        assertEquals(1, (int) query.fieldMaxs.get("cat"));
        query.filterFieldMax("cat", 0);
        assertEquals(0, (int) query.fieldMaxs.get("cat"));
    }

    @Test
    public void testFieldMin() {
        final Query query = new Query();
        query.filterFieldMin("cat", 1);
        assertEquals(1, (int) query.fieldMins.get("cat"));
        query.filterFieldMin("cat", 0);
        assertEquals(1, (int) query.fieldMins.get("cat"));
        query.filterFieldMin("cat", 2);
        assertEquals(2, (int) query.fieldMins.get("cat"));
    }

    @Test
    public void testTagsAll() {
        final Query query = new Query();
        query.filterTagsAll("one", "two");
        assertEquals(2, query.conjunctiveTags.size());
        query.filterTagsAll("two", "three");
        assertEquals(3, query.conjunctiveTags.size());
    }

    @Test
    public void testTagsAny() {
        final Query query = new Query();
        query.filterTagsAny("one", "two");
        assertEquals(1, query.disjunctiveTags.size());
        assertEquals(2, query.disjunctiveTags.get(0).size());
        query.filterTagsAny("two", "three");
        assertEquals(2, query.disjunctiveTags.size());
        assertEquals(2, query.disjunctiveTags.get(1).size());
    }

}
