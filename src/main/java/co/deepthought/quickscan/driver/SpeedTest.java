package co.deepthought.quickscan.driver;

import co.deepthought.quickscan.index.IndexShard;

public class SpeedTest {

    public static void main(String[] args) {
        final int top = 16384;

        final String[] resultIds = new String[top];
        final long[][] tags = new long[2][top];
        final double[][] fields = new double[3][top];
        final double[][] neutralScores = new double[2][top];
        final double[][] negativeScores = new double[2][top];
        final double[][] positiveScores = new double[2][top];
        for(int i = 0; i < top; i++) {
            resultIds[i] = "id-" + i;

            fields[0][i] = i; // [0-1023]
            fields[1][i] = top - i; // [1024 - 1]
            fields[2][i] = i % 16; // [0,1,2,...15,16,0,1,2,...]

            tags[0][i] |= (1L << (i % 16)); // [first word, one-hot, sliding rotation to 16]

            tags[1][i] |= (1L); // [second word, first bit always set]
            if(i % 2 == 0) {
                tags[1][i] |= (1L << 1); // [second word, first bit set for evens]
            }
        }

        final IndexShard shard = new IndexShard(
            resultIds,
            tags,
            fields,
            neutralScores,
            negativeScores,
            positiveScores
        );

        for(int i = 0; i < 1000; i++) {
            shard.filter(
                new long[] {0L, (1L | (1L << 1))}, // evens only... 512
                new long[][] {{0xFFL, 0L}}, // [0-7]/16 + [4-11]/16 = [4-7]... 128
                new double[] {256, 256, 3}, // drop bottom 1/4, 96 remain
                new double[] {786, 786, 5} // all 4,6 at this point, drop bottoms
            );
        }

        long start, end, total;
        total = 0;

        for(int i = 0; i < 1000; i++) {
            start = System.nanoTime();
            shard.filter(
                new long[] {0L, (1L | (1L << 1))}, // evens only... 512
                new long[][] {{0xFFL, 0L}}, // [0-7]/16 + [4-11]/16 = [4-7]... 128
                new double[] {256, 256, 3}, // drop bottom 1/4, 96 remain
                new double[] {786, 786, 5} // all 4,6 at this point, drop bottoms
            );
            end = System.nanoTime();
            total += (end - start);
        }

        System.out.println(total/1000);

    }


}
