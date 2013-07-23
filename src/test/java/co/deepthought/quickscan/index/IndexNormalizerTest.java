package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.Result;
import co.deepthought.quickscan.store.ResultStore;
import co.deepthought.quickscan.store.ResultTest;
import com.sleepycat.je.DatabaseException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class IndexNormalizerTest {

    private IndexNormalizer normalizer;

    @Before
    public void setUp() throws DatabaseException {
        final IndexMap map = IndexMapTest.getMockMapper();
        this.normalizer = new IndexNormalizer(map);
        for(final Result result : ResultTest.mock()) {
            this.normalizer.index(result);
        }
    }

    @Test
    public void testNormalizeResultIds() {
        assertArrayEquals(new String[] {"a", "b", "b", "c", "c", "d", "d", "d"}, this.normalizer.normalizeResultIds());
    }

    @Test
    public void testNormalizeMinFields() {
        final double[][] normalized = this.normalizer.normalizeMinFields();
        assertEquals(5, normalized.length);
        assertArrayEquals(new double[] {
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 100,
                100, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
            normalized[0], 0);
        assertArrayEquals(new double[] {
                Double.NEGATIVE_INFINITY, 10, 10, Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
            normalized[1], 0);
        assertArrayEquals(new double[] {
                Double.NEGATIVE_INFINITY, 11, 11, Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 12, Double.NEGATIVE_INFINITY},
            normalized[2], 0);
        assertArrayEquals(new double[] {
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
            normalized[3], 0);
        assertArrayEquals(new double[] {
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
            normalized[4], 0);
    }

    @Test
    public void testNormalizeScores() {
        final double[][] normalized = this.normalizer.normalizeScores();
        assertEquals(8, normalized.length);
        assertArrayEquals(new double[]{-1, -1, -1, -1, -1, 0.01}, normalized[0], 0);
        assertArrayEquals(new double[]{-1, 0.6, -1, 0.5, -1, 0.01}, normalized[1], 0);
        assertArrayEquals(new double[] {-1,0.6, -1, 0.5, -1, 0.99}, normalized[2], 0);
        assertArrayEquals(new double[] {0.99, 0.8, -1, -1, -1, 0.01}, normalized[3], 0);
        assertArrayEquals(new double[] {0.99, 0.8, -1, 0.25, -1, 0.99}, normalized[4], 0);
        assertArrayEquals(new double[] {-1, 0.8, -1, -1, -1, 0.01}, normalized[5], 0);
        assertArrayEquals(new double[] {-1, 0.8, -1, 0.75, -1, 0.99}, normalized[6], 0);
        assertArrayEquals(new double[] {-1, 0.8, -1, 0.5, -1, 0.99}, normalized[7], 0);
    }

    @Test
    public void testNormalizeTags() {
        final long[][] normalized = this.normalizer.normalizeTags();
        assertEquals(3, normalized.length);
        assertArrayEquals(new long[] {0x0L, 0x1L, 0x200003L, 0x2L, 0x2L, 0x4L, 0x6L, 0x6L}, normalized[0]);
        assertArrayEquals(new long[] {0x0L, 0x0L, 0x0L, 0x0L, 0x0L, 0x1000000000L, 0x1000000000L, 0x1000000000L},
            normalized[1]);
        assertArrayEquals(new long[] {0x8L, 0x8L, 0x4L, 0x9L, 0x5L, 0x18L, 0x14L, 0x14L}, normalized[2]);
    }

    @Test
    public void testTransposeDouble() {
        final double[][] in = new double[][] {
            {0, 1},
            {2, 3},
            {4, 5}
        };
        final double[][] out = IndexNormalizer.transpose(in);
        assertEquals(2, out.length);
        assertArrayEquals(new double[] {0, 2, 4}, out[0], 0);
        assertArrayEquals(new double[] {1, 3, 5}, out[1], 0);
    }

    @Test
    public void testTransposeLong() {
        final long[][] in = new long[][] {
            {0, 1, 2},
            {4, 5, 6},
        };
        final long[][] out = IndexNormalizer.transpose(in);
        assertEquals(3, out.length);
        assertArrayEquals(new long[] {0, 4}, out[0]);
        assertArrayEquals(new long[] {1, 5}, out[1]);
        assertArrayEquals(new long[] {2, 6}, out[2]);
    }

}
