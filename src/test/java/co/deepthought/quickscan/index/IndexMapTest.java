package co.deepthought.quickscan.index;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IndexMapTest {

    private IndexMap map;

    public static IndexMap getMockMapper() {
        final Set<String> tags = new LinkedHashSet<>();
        for(int i = 0 ;i < 130; i++) {
            tags.add(Integer.toString(i));
        }

        final Set<String> fields = new LinkedHashSet<>();
        for(int i = 0 ;i < 5; i++) {
            fields.add("field-" + Integer.toString(i));
        }

        final Set<String> scores = new LinkedHashSet<>();
        for(int i = 0; i < 5; i++) {
            scores.add("score-" + Integer.toString(i));
        }

        final Multimap<String, Double> scoreSamples = ArrayListMultimap.create();
        for(int i = 0; i < 5; i++) {
            for(double value = 0.0; value <= i * 10.0; value += 1.0) {
                scoreSamples.put("score-" + Integer.toString(i), value);
            }
        }
        return new IndexMap(tags, fields, scores, scoreSamples);
    }

    @Before
    public void setUp() {
        this.map = IndexMapTest.getMockMapper();
    }

    @Test
    public void testGetTagPage() {
        assertEquals(0, IndexMap.getTagPage(0));
        assertEquals(0, IndexMap.getTagPage(1));
        assertEquals(0, IndexMap.getTagPage(63));
        assertEquals(1, IndexMap.getTagPage(64));
        assertEquals(1, IndexMap.getTagPage(127));
        assertEquals(2, IndexMap.getTagPage(128));
    }

    @Test
    public void testGetTagMask() {
        assertEquals(0x1L, IndexMap.getTagMask(0));
        assertEquals(0x2L, IndexMap.getTagMask(1));
        assertEquals(0x8L, IndexMap.getTagMask(3));
        assertEquals(0x8000000000000000L, IndexMap.getTagMask(63));
        assertEquals(0x1L, IndexMap.getTagMask(64));
    }

    @Test
    public void testMapNames() {
        final Set<String> names = new HashSet<String>();
        names.add("a");
        names.add("b");
        names.add("c");
        final Map<String, Integer> nameMap = IndexMap.mapNames(names);
        assertEquals(names, nameMap.keySet());
        assertTrue(nameMap.values().contains(0));
        assertTrue(nameMap.values().contains(1));
        assertTrue(nameMap.values().contains(2));
    }

    @Test
    public void testNormalizeFields() {
        final Map<String, Double> fields = new HashMap<String, Double>();
        fields.put("field-0", 1.2);
        fields.put("field-3", 0.5);
        fields.put("field-FAKE", 99.0);
        final double[] normalized = this.map.normalizeFields(fields, -1);
        assertArrayEquals(new double[] {1.2, -1, -1, 0.5, -1}, normalized, 0);
    }

    @Test
    public void testNormalizeScores() {
        final Map<String, Double> fields = new HashMap<String, Double>();
        fields.put("score-0", 0.0);
        fields.put("score-1", 5.0);
        fields.put("score-3", 7.5);
        fields.put("score-100", 0.2);
        final double[] normalized = this.map.normalizeScores(fields, 0, true);
        assertArrayEquals(new double[] {0.99, 0.5, 0, 0.25, 0}, normalized, 0);
    }

    @Test
    public void testNormalizeTags() {
        final List<String> tags = Arrays.asList("0", "10", "80", "121", "129", "babyfat");
        final long[] normalized = this.map.normalizeTags(tags);
        assertEquals(3, normalized.length);
        assertEquals(0x0000000000000401L, normalized[0]);
        assertEquals(0x0200000000010000L, normalized[1]);
        assertEquals(0x0000000000000012L, normalized[2]);
    }

}