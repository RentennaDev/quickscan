package co.deepthought.quickscan.index;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PercentilerTest {

    @Test
    public void testInterpolate() {
        final double[] numbers = new double[] {1.0, 2.0, 3.0, 4.0, 5.0};
        Assert.assertEquals(1.0, Percentiler.interpolate(numbers, 0.0), 0);
        Assert.assertEquals(1.5, Percentiler.interpolate(numbers, 0.125), 0);
        Assert.assertEquals(2.0, Percentiler.interpolate(numbers, 0.25), 0);
        Assert.assertEquals(2.5, Percentiler.interpolate(numbers, 0.375), 0);
        Assert.assertEquals(3.0, Percentiler.interpolate(numbers, 0.50), 0);
        Assert.assertEquals(3.5, Percentiler.interpolate(numbers, 0.625), 0);
        Assert.assertEquals(4.0, Percentiler.interpolate(numbers, 0.75), 0);
        Assert.assertEquals(4.5, Percentiler.interpolate(numbers, 0.875), 0);
        Assert.assertEquals(5.0, Percentiler.interpolate(numbers, 1.0), 0);
    }

    @Test
    public void testSort() {
        final List<Double> numbers = new ArrayList<Double>();
        numbers.add(1.0);
        numbers.add(2.0);
        numbers.add(5.0);
        numbers.add(1.5);
        final double[] sorted = Percentiler.sort(numbers);
        Assert.assertArrayEquals(sorted, new double[] {1.0, 1.5, 2.0, 5.0}, 0);
    }

}
