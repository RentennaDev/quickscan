package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.DocumentStore;
import co.deepthought.quickscan.store.DocumentTest;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class IndexNormalizerTest {

    private IndexNormalizer normalizer;

    @Before
    public void setUp() throws SQLException {
        final IndexMap map = IndexMapTest.getMockMapper();
        this.normalizer = new IndexNormalizer(map);
        final DocumentStore store = new DocumentStore(":memory:");
        for(final Document document : DocumentTest.mockDocuments(store)) {
            this.normalizer.indexDocument(document);
        }
    }

    @Test
    public void testNormalizeResultIds() {
        assertArrayEquals(new String[] {"a", "b", "c", "c"}, this.normalizer.normalizeResultIds());
    }

    @Test
    public void testNormalizeFields() {
        final double[][] normalized = this.normalizer.normalizeFields();
        assertEquals(5, normalized.length);
        assertArrayEquals(new double[] {Double.NaN, Double.NaN, 100, Double.NaN}, normalized[0], 0);
        assertArrayEquals(new double[] {Double.NaN, 10, Double.NaN, Double.NaN}, normalized[1], 0);
        assertArrayEquals(new double[] {Double.NaN, 11, Double.NaN, Double.NaN}, normalized[2], 0);
        assertArrayEquals(new double[] {Double.NaN, Double.NaN, Double.NaN, Double.NaN}, normalized[3], 0);
        assertArrayEquals(new double[] {Double.NaN, Double.NaN, Double.NaN, Double.NaN}, normalized[4], 0);
    }

    @Test
    public void testNormalizeScores() {
        final double[][] normalized = this.normalizer.normalizeScores();
        assertEquals(4, normalized.length);
        assertArrayEquals(new double[] {-1, -1, -1, -1, -1}, normalized[0], 0);
        assertArrayEquals(new double[] {-1, 0.6, -1, 0.5, -1}, normalized[1], 0);
        assertArrayEquals(new double[] {0.4, 0.8, -1, -1, -1}, normalized[2], 0);
        assertArrayEquals(new double[] {-1, 0.8, -1, -1, -1}, normalized[3], 0);
    }

    @Test
    public void testNormalizeTags() {
        final long[][] normalized = this.normalizer.normalizeTags();
        assertEquals(3, normalized.length);
        assertArrayEquals(new long[] {0x0L, 0x1L, 0x2L, 0x4L}, normalized[0]);
        assertArrayEquals(new long[] {0x0L, 0x0L, 0x0L, 0x1000000000L}, normalized[1]);
        assertArrayEquals(new long[] {0x0L, 0x0L, 0x1L, 0x4L}, normalized[2]);
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
