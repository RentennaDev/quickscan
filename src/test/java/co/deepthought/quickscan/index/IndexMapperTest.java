package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Score;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IndexMapperTest {

    private IndexMapper mapper;

    public static IndexMapper getMockMapper() {
        final List<String> tags = new ArrayList<String>();
        for(int i = 0 ;i < 130; i++) {
            tags.add(Integer.toString(i));
        }
        final List<String> fields = new ArrayList<String>();
        for(int i = 0 ;i < 5; i++) {
            fields.add("field-" + Integer.toString(i));
        }
        final List<String> scoresNeu = new ArrayList<String>();
        for(int i = 0 ;i < 5; i++) {
            scoresNeu.add("neutral-" + Integer.toString(i));
        }
        final List<String> scoresNeg = new ArrayList<String>();
        for(int i = 0 ;i < 5; i++) {
            scoresNeg.add("negative-" + Integer.toString(i));
        }
        final List<String> scoresPos = new ArrayList<String>();
        for(int i = 0 ;i < 5; i++) {
            scoresPos.add("positive-" + Integer.toString(i));
        }
        return new IndexMapper(tags, fields, scoresNeu, scoresNeg, scoresPos);
    }

    @Before
    public void setUp() {
        this.mapper = IndexMapperTest.getMockMapper();
    }

    @Test
    public void testGetTagPage() {
        assertEquals(0, IndexMapper.getTagPage(0));
        assertEquals(0, IndexMapper.getTagPage(1));
        assertEquals(0, IndexMapper.getTagPage(63));
        assertEquals(1, IndexMapper.getTagPage(64));
        assertEquals(1, IndexMapper.getTagPage(127));
        assertEquals(2, IndexMapper.getTagPage(128));
    }

    @Test
    public void testGetTagMask() {
        assertEquals(0x1L, IndexMapper.getTagMask(0));
        assertEquals(0x2L, IndexMapper.getTagMask(1));
        assertEquals(0x8L, IndexMapper.getTagMask(3));
        assertEquals(0x8000000000000000L, IndexMapper.getTagMask(63));
        assertEquals(0x1L, IndexMapper.getTagMask(64));
    }

    @Test
    public void testMapNames() {
        final Set<String> names = new HashSet<String>();
        names.add("a");
        names.add("b");
        names.add("c");
        final Map<String, Integer> nameMap = IndexMapper.mapNames(names);
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
        final double[] normalized = this.mapper.normalizeFields(fields, -1);
        assertArrayEquals(new double[] {1.2, -1, -1, 0.5, -1}, normalized, 0);
    }

    @Test
    public void testNormalizeScoresNegative() {
        final Map<String, Double> fields = new HashMap<String, Double>();
        fields.put("negative-0", 0.7);
        fields.put("negative-3", 0.5);
        fields.put("neutral-1", 0.2);
        final double[] normalized = this.mapper.normalizeScores(fields, Score.Valence.NEGATIVE, 0);
        assertArrayEquals(new double[] {0.7, 0, 0, 0.5, 0}, normalized, 0);
    }

    @Test
    public void testNormalizeScoresNeutral() {
        final Map<String, Double> fields = new HashMap<String, Double>();
        fields.put("neutral-0", 0.7);
        fields.put("neutral-3", 0.5);
        fields.put("neutral-FAKE", 0.2);
        final double[] normalized = this.mapper.normalizeScores(fields, Score.Valence.NEUTRAL, 0);
        assertArrayEquals(new double[] {0.7, 0, 0, 0.5, 0}, normalized, 0);
    }

    @Test
    public void testNormalizeScoresPositive() {
        final Map<String, Double> fields = new HashMap<String, Double>();
        fields.put("positive-0", 0.7);
        fields.put("positive-3", 0.5);
        fields.put("neutral-1", 0.2);
        final double[] normalized = this.mapper.normalizeScores(fields, Score.Valence.POSITIVE, 0);
        assertArrayEquals(new double[] {0.7, 0, 0, 0.5, 0}, normalized, 0);
    }

    @Test
    public void testNormalizeTags() {
        final List<String> tags = Arrays.asList("0", "10", "80", "121", "129", "babyfat");
        final long[] normalized = this.mapper.normalizeTags(tags);
        assertEquals(3, normalized.length);
        assertEquals(0x0000000000000401L, normalized[0]);
        assertEquals(0x0200000000010000L, normalized[1]);
        assertEquals(0x0000000000000006L, normalized[2]);
    }

}