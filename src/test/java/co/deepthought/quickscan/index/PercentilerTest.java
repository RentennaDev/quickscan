package co.deepthought.quickscan.index;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PercentilerTest {

    @Test
    public void testComputeBreakpoints() {
        final double[] numbers = new double[] {1.0, 2.0, 3.0, 4.0, 5.0};
        final double[] breakpoints = Percentiler.computeBreakpoints(numbers);
        Assert.assertTrue(breakpoints[0] > 1.0);
        Assert.assertEquals(breakpoints[24], 2.0, 0);
        Assert.assertEquals(breakpoints[49], 3.0, 0);
        Assert.assertEquals(breakpoints[74], 4.0, 0);
        Assert.assertTrue(breakpoints[98] < 5.0);
    }

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
    public void testPercentile() {
        final List<Double> numbers = new ArrayList<>();
        for(double value = 0; value <= 1000.; value += 1.0) {
            numbers.add(value);
        }
        final Percentiler percentiler = new Percentiler(numbers);
        Assert.assertEquals(0.01, percentiler.percentile(-500), 0);
        Assert.assertEquals(0.99, percentiler.percentile(5000), 0);
        Assert.assertEquals(0.3, percentiler.percentile(300), 0);
        Assert.assertEquals(0.5, percentiler.percentile(500), 0);
        Assert.assertEquals(0.015, percentiler.percentile(15), 0);
    }

    @Test
    public void testPercentileLowRes() {
        final List<Double> numbers = new ArrayList<>();
        for(double value = 0; value <= 10.; value += 1.0) {
            numbers.add(value);
        }
        final Percentiler percentiler = new Percentiler(numbers);
        Assert.assertEquals(0.01, percentiler.percentile(0), 0);
        Assert.assertEquals(0.99, percentiler.percentile(10), 0);
        Assert.assertEquals(0.3, percentiler.percentile(3), 0);
        Assert.assertEquals(0.5, percentiler.percentile(5), 0);
        Assert.assertEquals(0.015, percentiler.percentile(.15), 0);
    }

    @Test
    public void testPercentileWall() {
        final List<Double> numbers = new ArrayList<>();
        for(double value = 0; value < 10.; value += 1.0) {
            numbers.add(0.0);
        }
        for(double value = 0; value <= 10.; value += 1.0) {
            numbers.add(value);
        }
        final Percentiler percentiler = new Percentiler(numbers);
        Assert.assertEquals(0.01, percentiler.percentile(-1), 0);
        Assert.assertEquals(0.5, percentiler.percentile(0), 0);
        Assert.assertEquals(0.55, percentiler.percentile(1), 0);
        Assert.assertEquals(0.525, percentiler.percentile(0.5), 0);
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
