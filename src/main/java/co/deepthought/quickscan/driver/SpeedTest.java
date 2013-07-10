package co.deepthought.quickscan.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpeedTest {

    public static void main(String[] args) {
        int numPages = 1280;

        final Random generator = new Random();

        final List<int[]> numberss = new ArrayList<int[]>();
        for(int i = 0; i < numPages; i ++) {
            int COUNT = generator.nextInt(65536);
            final int[] numbers = new int[COUNT];
            SpeedTest.fill(numbers);
            numberss.add(numbers);
        }

        int sum;

        //Do 1000 trials to heat up the cache
        for(int i = 0; i < 1000; i++) {
            final int[] numbers = numberss.get(generator.nextInt(numPages));
            sum = 0;
            for(int j = 0; j < numbers.length; j++) {
                sum += numbers[j];
            }
        }

        //Now the real test
        long start, end;
        long timesum = 0;
        int countsum = 0;
        for(int i = 0; i < 1000; i++) {
            final int[] numbers = numberss.get(generator.nextInt(numPages));
            start = System.nanoTime();
            sum = 0;
            for(int j = 0; j < numbers.length; j++) {
                sum += numbers[j];
            }
            end = System.nanoTime();
            timesum += (end-start);
            countsum += numbers.length;
        }

        System.out.println(timesum);
        System.out.println(countsum/1000);
        System.out.print(timesum/(countsum/1000));
    }

    public static void fill(final int[] numbers) {
        final Random generator = new Random();
        for(int i = 0; i < numbers.length; i++) {
            numbers[i] = generator.nextInt();
        }
    }

}
