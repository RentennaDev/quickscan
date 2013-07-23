package co.deepthought.quickscan.index;

import org.junit.Before;
import org.junit.Test;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertArrayEquals;

public class IndexShardTest {

    private boolean[] nonmatches;
    private IndexShard shard;

    @Before
    public void setUp() {
        final String[] resultIds = new String[1024];
        final long[][] tags = new long[2][1024];
        final double[][] fields = new double[3][1024];
        final double[][] scores = new double[1024][4];
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

            scores[i][0] = (1+i)/1026.;
            scores[i][1] = 1.-scores[i][0];
            scores[i][2] = (1 + (i % 128))/130.;

            if(i % 4 == 0) {
                scores[i][3] = 0.01;
            }
            else if(i % 4 == 1) {
                scores[i][3] = 0.5;
            }
            else if(i % 4 == 2) {
                scores[i][3] = 0.99;
            }
            else {
                scores[i][3] = -1;
            }
        }
        this.shard = new IndexShard(
            resultIds,
            tags,
            fields,
            fields,
            scores
        );
        this.nonmatches = new boolean[1024];
    }

    @Test
    public void testFilter() {
        this.nonmatches = this.shard.filter(
            new long[] {0L, (1L | (1L << 1))}, // evens only... 512
            new long[][] {{0xFFL, 0L}, {0xFF0L, 0L}}, // [0-7]/16 + [4-11]/16 = [4-7]... 128
            new double[] {256, Double.NaN, Double.NaN}, // drop bottom 1/4, 96 remain
            new double[] {Double.NaN, Double.NaN, 5} // all 4,6 at this point, drop bottoms
        );
        for(int i = 0; i < this.nonmatches.length; i++) {
            if(!this.nonmatches[i]) {
                assertTrue(i >= 256);
                assertTrue(i % 16 == 4);
            }
        }
        assertEquals(48, this.countMatches());
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

    @Test
    public void testSortToBucketsFiltered() {
        this.nonmatches[0] = true;
        this.nonmatches[128] = true;
        this.nonmatches[512] = true;
        final double[] preferences = new double[] {1.0, 0.0, 0.0, 0.0};
        final List<String>[] buckets = this.shard.getBuckets();
        this.shard.sortToBuckets(buckets, this.nonmatches, preferences);
        assertTrue(this.bucketOf(buckets, "id-1023") < this.bucketOf(buckets, "id-786"));
        assertEquals(this.bucketOf(buckets, "id-0"), -1);
        assertEquals(this.bucketOf(buckets, "id-128"), -1);
        assertEquals(this.bucketOf(buckets, "id-512"), -1);
    }

    @Test
    public void testSortToBucketsMissingValues() {
        final double[] preferences = new double[] {0.0, 0.0, 0.0, 1.0};
        final List<String>[] buckets = this.shard.getBuckets();
        this.shard.sortToBuckets(buckets, this.nonmatches, preferences);
        assertTrue(this.bucketOf(buckets, "id-2") < this.bucketOf(buckets, "id-1"));
        assertTrue(this.bucketOf(buckets, "id-3") < this.bucketOf(buckets, "id-1"));
        assertTrue(this.bucketOf(buckets, "id-3") < this.bucketOf(buckets, "id-0"));
        assertTrue(this.bucketOf(buckets, "id-1003") < this.bucketOf(buckets, "id-1002"));
    }

    @Test
    public void testSortToBucketsOneDimensional() {
        final double[] preferences = new double[] {1.0, 0.0, 0.0, 0.0};
        final List<String>[] buckets = this.shard.getBuckets();
        this.shard.sortToBuckets(buckets, this.nonmatches, preferences);
        assertTrue(this.bucketOf(buckets, "id-1023") < this.bucketOf(buckets, "id-786"));
        assertTrue(this.bucketOf(buckets, "id-786") < this.bucketOf(buckets, "id-512"));
        assertTrue(this.bucketOf(buckets, "id-512") < this.bucketOf(buckets, "id-256"));
        assertTrue(this.bucketOf(buckets, "id-256") < this.bucketOf(buckets, "id-0"));
    }

    @Test
    public void testSortToBucketsThreeDimensional() {
        final double[] preferences = new double[] {1.0, 1.0, 1.0, 0.0}; //equivalen to just third score
        final List<String>[] buckets = this.shard.getBuckets();
        this.shard.sortToBuckets(buckets, this.nonmatches, preferences);
        assertTrue(this.bucketOf(buckets, "id-1023") < this.bucketOf(buckets, "id-1021"));
        assertTrue(this.bucketOf(buckets, "id-127") < this.bucketOf(buckets, "id-128"));
        assertTrue(this.bucketOf(buckets, "id-511") < this.bucketOf(buckets, "id-512"));
        assertTrue(this.bucketOf(buckets, "id-120") < this.bucketOf(buckets, "id-11"));
    }

    @Test
    public void testSortToBucketsTwoDimensional() {
        final double[] preferences = new double[] {1.0, 1.0, 0.0, 0.0}; //inverse, so all should be same bucket
        final List<String>[] buckets = this.shard.getBuckets();
        this.shard.sortToBuckets(buckets, this.nonmatches, preferences);
        assertEquals(this.bucketOf(buckets, "id-1023"), this.bucketOf(buckets, "id-786"));
        assertEquals(this.bucketOf(buckets, "id-786"), this.bucketOf(buckets, "id-512"));
        assertEquals(this.bucketOf(buckets, "id-512"), this.bucketOf(buckets, "id-256"));
        assertEquals(this.bucketOf(buckets, "id-256"), this.bucketOf(buckets, "id-0"));
    }

    @Test
    public void testTrimBukets() {
        final List[] buckets = new List[4];
        buckets[0] = new ArrayList();
        buckets[1] = new ArrayList();
        buckets[2] = new ArrayList();
        buckets[3] = new ArrayList();
        buckets[0].add("A");
        buckets[0].add("B");
        buckets[1].add("C");
        buckets[3].add("B");
        buckets[3].add("D");
        final PaginatedResults<String> result = this.shard.trimBuckets(buckets, 100);
        final String[] expected = new String[] {"A", "B", "C", "D"};
        assertArrayEquals(expected, result.getResults().toArray(new String[4]));
    }

    private void assertMatches(final int index) {
        assertFalse(this.nonmatches[index]);
    }

    private void assertNotMatches(final int index) {
        assertTrue(this.nonmatches[index]);
    }

    private int bucketOf(final List<String>[] buckets, final String find) {
        int count = 0;
        for(final List<String> bucket : buckets) {
            for(final String resultId : bucket) {
                if(resultId.equals(find)) {
                    return count;
                }
            }
            count++;
        }
        return -1;
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
