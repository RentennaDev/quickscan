package co.deepthought.quickscan.index;

import org.junit.Test;

import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchResultTest {

    @Test
    public void testInSet() {
        final SearchResult r1 = new SearchResult("a", 0);
        final SearchResult r2 = new SearchResult("b", 0);
        final SearchResult r3 = new SearchResult("a", 0);
        final LinkedHashSet<SearchResult> lhs = new LinkedHashSet<>();
        lhs.add(r1);
        lhs.add(r2);
        lhs.add(r3);
        assertEquals(2, lhs.size());
        assertTrue(lhs.contains(new SearchResult("a", 0)));
        assertTrue(lhs.contains(new SearchResult("b", 0)));
    }

}
