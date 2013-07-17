package co.deepthought.quickscan.index;

import java.util.Arrays;
import java.util.Collection;

/**
 *  Given a collection of numbers, estimate the percentile for new incoming values.
 */
public class Percentiler {

    private final double[] breakpoints;

    public Percentiler(final Collection<Double> samples) {
        final double[] sampleArray = Percentiler.sort(samples);
        this.breakpoints = Percentiler.computeBreakpoints(sampleArray);
    }

    public double percentile(double value) {
        if(value < this.breakpoints[0]) {
            return 0.01;
        }
        else {
            for(int i = 1; i < 99; i++) {
                final double next = this.breakpoints[i];
                if(next > value) {
                    final double previous = this.breakpoints[i-1];
                    final double width = next - previous;
                    final double offset = value - previous;
                    final double proportion = offset / width;
                    return (i/100.) + (0.01 * proportion);
                }
            }
            // no breakpoint greater
            return 0.99;
        }
    }

    public static double[] computeBreakpoints(final double[] sortedSamples) {
        final double[] breakpoints =  new double[99];
        for(int i = 1; i < 100; i++) {
            breakpoints[i-1] = Percentiler.interpolate(sortedSamples, i / 100.);
        }
        return breakpoints;
    }

    public static double interpolate(final double[] samples, final double location) {
        final double width = (samples.length - 1.0);
        final double x1 = Math.floor(location * width);
        final double x2 = Math.ceil(location * width);
        final double xD = (location * width) - x1;
        final double y1 = samples[(int)x1];
        final double y2 = samples[(int)x2];
        final double yD = y2 - y1;
        return y1 + yD * xD;
    }

    public static double[] sort(final Collection<Double> samples) {
        final double[] sampleArray = new double[samples.size()];
        int index = 0;
        for(final Double sample :samples) {
            sampleArray[index] = sample;
            index++;
        }
        Arrays.sort(sampleArray);
        return sampleArray;
    }

}
