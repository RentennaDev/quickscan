package co.deepthought.quickscan.index;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SearcherTest {

    private Searcher searcher;

    @Before
    public void setUp() {
        final IndexMapper mapper= IndexMapperTest.getMockMapper();
        this.searcher = new Searcher(mapper, null);
    }

    @Test
    public void testNormalizeDisjunctiveTags() {
        final List<List<String>> tagSets = new ArrayList<List<String>>();

        final List<String> tags1 = new ArrayList<String>();
        tagSets.add(tags1);
        tags1.add("0");
        tags1.add("128");
        tags1.add("baby");

        final List<String> tags2 = new ArrayList<String>();
        tagSets.add(tags2);
        tags2.add("1");
        tags2.add("129");

        final long[][] normalized = this.searcher.normalizeDisjunctiveTags(tagSets);
        assertEquals(2, normalized.length);
        assertEquals(0x1L, normalized[0][0]);
        assertEquals(0x0L, normalized[0][1]);
        assertEquals(0x5L, normalized[0][2]);
        assertEquals(0x2L, normalized[1][0]);
        assertEquals(0x0L, normalized[1][1]);
        assertEquals(0x2L, normalized[1][2]);
    }

}
