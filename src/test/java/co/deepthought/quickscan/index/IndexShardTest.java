package co.deepthought.quickscan.index;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class IndexShardTest {

    private boolean[] nonmatches;
    private IndexShard shard;

    @Before
    public void setUp() {
        final String[] resultIds = new String[1024];
        final long[][] tags = new long[2][1024];
        final double[][] fields = new double[3][1024];
        final double[][] neutralScores = new double[2][1024];
        final double[][] negativeScores = new double[2][1024];
        final double[][] positiveScores = new double[2][1024];
        for(int i = 0; i < 1024; i++) {
            resultIds[i] = "id-" + i;

            fields[0][i] = i; // [0-1023]
            fields[1][i] = 1024 - i; // [1024 - 1]
            fields[2][i] = i % 16; // [0,1,2,...15,16,0,1,2,...]

            tags[0][i] |= (1L << (i % 16)); // [first word, one-hot, sliding rotation to 16]

            tags[1][i] |= (1L); // [second word, first bit always set]
            if(i % 2 == 0) {
                tags[1][i] |= (1L << 1); // [second word, first bit set for evens]
            }
        }
        this.shard = new IndexShard(
            resultIds,
            tags,
            fields,
            neutralScores,
            negativeScores,
            positiveScores
        );
        this.nonmatches = new boolean[1024];
    }

    @Test
    public void testFilterConjunctive() {
        this.shard.filterConjunctive(this.nonmatches, 1, 0);
        assertEquals(1024, this.countMatches());
        this.shard.filterConjunctive(this.nonmatches, 1, 1L);
        assertEquals(1024, this.countMatches());
        this.shard.filterConjunctive(this.nonmatches, 1, (1L) | (1L << 1));
        assertEquals(512, this.countMatches());
        assertMatches(0);
        assertNotMatches(1);
        this.shard.filterConjunctive(this.nonmatches, 0, (1L << 2));
        assertEquals(64, this.countMatches());
        assertMatches(2);
        assertNotMatches(4);
        this.shard.filterConjunctive(this.nonmatches, 0, (1L << 2));
        assertEquals(64, this.countMatches());
        this.shard.filterConjunctive(this.nonmatches, 0, (1L << 2) | (1L << 1));
        assertEquals(0, this.countMatches());
    }

    @Test
    public void testFilterDistjunctive() {
        final long[] comparison1 = new long[] {0L, 1L};
        this.shard.filterDisjunctive(this.nonmatches, comparison1);
        assertEquals(1024, this.countMatches());
        final long[] comparison2 = new long[] {0L, (1L | (1L << 1))};
        this.shard.filterDisjunctive(this.nonmatches, comparison2);
        assertEquals(1024, this.countMatches());
        final long[] comparison3 = new long[] {(1L | (1L << 1)), 0L};
        this.shard.filterDisjunctive(this.nonmatches, comparison3);
        assertEquals(128, this.countMatches());
        this.assertMatches(0);
        this.assertMatches(1);
        this.assertNotMatches(2);
        this.assertNotMatches(15);
        this.assertMatches(16);
        final long[] comparison4 = new long[] {(1L << 48), (1L << 1)};
        this.shard.filterDisjunctive(this.nonmatches, comparison4);
        assertEquals(64, this.countMatches());
        this.assertMatches(0);
        this.assertNotMatches(1);
        final long[] comparison5 = new long[] {(1L << 48), (1L << 48)};
        this.shard.filterDisjunctive(this.nonmatches, comparison5);
        assertEquals(0, this.countMatches());
    }

    @Test
    public void testFilterDistjunctiveEmpty() {
        final long[] comparison = new long[] {0L, 0L};
        this.shard.filterDisjunctive(this.nonmatches, comparison);
        assertEquals(0, this.countMatches());
    }

    @Test
    public void testFilterMax() {
        this.shard.filterMax(this.nonmatches, 0, Double.NaN);
        assertEquals(1024, this.countMatches());
        this.shard.filterMax(this.nonmatches, 0, 255);
        this.assertMatches(0);
        this.assertMatches(80);
        this.assertMatches(255);
        this.assertNotMatches(256);
        this.assertNotMatches(1023);
        assertEquals(256, this.countMatches());
        this.shard.filterMax(this.nonmatches, 2, 7);
        assertEquals(128, this.countMatches());
        this.assertMatches(0);
        this.assertMatches(7);
        this.assertNotMatches(8);
        this.assertNotMatches(256);
    }

    @Test
    public void testFilterMin() {
        this.shard.filterMin(this.nonmatches, 0, Double.NaN);
        assertEquals(1024, this.countMatches());
        this.shard.filterMin(this.nonmatches, 0, 256);
        this.assertMatches(256);
        this.assertMatches(800);
        this.assertMatches(1023);
        this.assertNotMatches(0);
        this.assertNotMatches(0);
        assertEquals(768, this.countMatches());
        this.shard.filterMin(this.nonmatches, 2, 8);
        assertEquals(384, this.countMatches());
        this.assertNotMatches(8);
        this.assertNotMatches(256);
        this.assertNotMatches(263);
        this.assertMatches(264);
        this.assertMatches(271);
    }

    private void assertMatches(final int index) {
        assertFalse(this.nonmatches[index]);
    }

    private void assertNotMatches(final int index) {
        assertTrue(this.nonmatches[index]);
    }

    private int countMatches() {
        int sum = 0;
        for(final boolean current : this.nonmatches) {
            if(!current) {
                sum += 1;
            }
        }
        return sum;
    }

}
