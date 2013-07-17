package co.deepthought.quickscan;

import java.util.*;

public class Tester {

    public static void main(String[] args) {
        final Random random = new Random();
        final List<Double> numbers = new ArrayList<>();
        for(int i = 0; i < 400; i++) {
            numbers.add(random.nextDouble());
        }

        long total = 0;
        for(int i = 0; i < 1000; i++) {
            final List newList = new ArrayList<>(numbers);
            final long start = System.nanoTime();

            Collections.sort(newList);

            final long end = System.nanoTime();
            total += (end-start);
        }
        System.out.println(total/1000);

        total = 0;
        for(int i = 0; i < 1000; i++) {
            final long start = System.nanoTime();

            final int size = 128;
            final List[] buckets = new List[size];
            for(int j = 0; j < size; j++) {
                buckets[j] = new ArrayList();
            }
            for(final Double number : numbers) {
                final int position = (int) ((1.0-number) * size);
                buckets[position].add(number);
            }

            final long end = System.nanoTime();
            total += (end-start);
        }
        System.out.println(total/1000);
    }

}
