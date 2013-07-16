package co.deepthought.quickscan.index;

import org.junit.Test;

import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchResultTest {

    @Test
    public void testInSet() {
        final SearchResult r1 = new SearchResult("a", new double[] {0.5});
        final SearchResult r2 = new SearchResult("b", new double[] {0.7});
        final SearchResult r3 = new SearchResult("a", new double[] {0.6});
        final LinkedHashSet<SearchResult> lhs = new LinkedHashSet<SearchResult>();
        lhs.add(r1);
        lhs.add(r2);
        lhs.add(r3);
        assertEquals(2, lhs.size());
        assertTrue(lhs.contains(new SearchResult("a", null)));
        assertTrue(lhs.contains(new SearchResult("b", null)));
    }

}
