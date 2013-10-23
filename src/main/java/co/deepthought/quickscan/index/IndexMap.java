package co.deepthought.quickscan.index;

import com.google.common.collect.Multimap;

import java.util.*;

/**
 * Stores mappings from denormalized fields to the normalized field indexes for a particular dataset.
 */
public class IndexMap {

    final private Map<String, Integer> tags;
    final private Map<String, Integer> fields;
    final private Map<String, Integer> scores;
    final private Map<String, Percentiler> percentilers;

    public static long getTagMask(final int tagIndex) {
        return 1L << (tagIndex % Long.SIZE);
    }

    public static int getTagPage(final int tagIndex) {
        return tagIndex / Long.SIZE;
    }

    public static Map<String, Integer> mapNames(final Set<String> names) {
        final Map<String, Integer> nameMap = new HashMap<>();
        int current = 0;
        for(final String name : names) {
            nameMap.put(name, current);
            current++;
        }
        return nameMap;
    }

    public static Map<String, Percentiler> mapPercentiles(final Multimap<String, Double> scores) {
        final Map<String, Percentiler> percentilers = new HashMap<>();
        for(final Map.Entry<String, Collection<Double>> entry : scores.asMap().entrySet()) {
            percentilers.put(entry.getKey(), new Percentiler(entry.getValue()));
        }
        return percentilers;
    }

    public IndexMap(
        final Set<String> tagNames,
        final Set<String> fieldNames,
        final Set<String> scoreNames,
        final Multimap<String, Double> scores
    ) {
        // add the special tag
        tagNames.add("_unknown");
        this.tags = IndexMap.mapNames(tagNames);
        this.fields = IndexMap.mapNames(fieldNames);
        this.scores = IndexMap.mapNames(scoreNames);
        this.percentilers = IndexMap.mapPercentiles(scores);
    }

    public int getTagPageCount() {
        return (int) Math.ceil(((double) this.tags.size()) / Long.SIZE);
    }

    public double[] normalizeFields(final Map<String, Double> values, final double defaultValue) {
        final double[] numbers = new double[this.fields.size()];
        Arrays.fill(numbers, defaultValue);
        for(final Map.Entry<String, Double> value : values.entrySet()) {
            final Integer index = this.fields.get(value.getKey());
            if(index != null) {
                numbers[index] = value.getValue();
            }
        }
        return numbers;
    }

    public double[] normalizeScores(final Map<String, Double> values, double defaultValue, boolean project) {
        final double[] numbers = new double[this.scores.size()];
        Arrays.fill(numbers, defaultValue);
        for(final Map.Entry<String, Double> entry : values.entrySet()) {
            final Integer index = this.scores.get(entry.getKey());
            if(index != null) {
                if(project) {
                    final Percentiler percentiler = this.percentilers.get(entry.getKey());
                    if(percentiler != null) {
                        // TODO: why would this happen? only phantoms?
                        numbers[index] = percentiler.percentile(entry.getValue());
                    }
                }
                else {
                    numbers[index] = entry.getValue();
                }
            }
        }
        return numbers;
    }

    public long[] normalizeTags(final Collection<String> tagNames) {
        final long[] bits = new long[this.getTagPageCount()];
        for(final String tagName : tagNames) {
            Integer tagIndex = this.tags.get(tagName);
            if(tagIndex == null) {
                tagIndex = this.tags.get("_unknown");
            }
            bits[IndexMap.getTagPage(tagIndex)] |= IndexMap.getTagMask(tagIndex);
        }
        return bits;
    }

    public Map<String, Double> projectScores(final Map<String, Double> values) {
        final Map<String, Double> projected = new HashMap<>();
        for(final Map.Entry<String, Double> entry : values.entrySet()) {
            final Percentiler percentiler = this.percentilers.get(entry.getKey());
            if(percentiler != null) {
                projected.put(entry.getKey(), percentiler.percentile(entry.getValue()));
            }
        }
        return projected;
    }

}
