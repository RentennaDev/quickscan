package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Score;

import java.util.*;

public class IndexMapper {

    final private Map<String, Integer> tags;
    final private Map<String, Integer> fields;
    final private Map<String, Integer> neutralScores;
    final private Map<String, Integer> negativeScores;
    final private Map<String, Integer> positiveScores;

    public static long getTagMask(final int tagIndex) {
        return 1L << (tagIndex % Long.SIZE);
    }

    public static int getTagPage(final int tagIndex) {
        return tagIndex / Long.SIZE;
    }

    public static Map<String, Integer> mapNames(final Iterable<String> names) {
        final Map<String, Integer> nameMap = new HashMap<String, Integer>();
        int current = 0;
        for(final String name : names) {
            nameMap.put(name, current);
            current++;
        }
        return nameMap;
    }

    public IndexMapper(
            final Iterable<String> tagNames,
            final Iterable<String> fieldNames,
            final Iterable<String> neutralScoreNames,
            final Iterable<String> negativeScoreNames,
            final Iterable<String> positiveScoreNames
        ) {
        this.tags = IndexMapper.mapNames(tagNames);
        this.fields = IndexMapper.mapNames(fieldNames);
        this.neutralScores = IndexMapper.mapNames(neutralScoreNames);
        this.negativeScores = IndexMapper.mapNames(negativeScoreNames);
        this.positiveScores = IndexMapper.mapNames(positiveScoreNames);
    }

    public int getTagPageCount() {
        return (int) Math.ceil((1.0 + this.tags.size()) / Long.SIZE);
    }

    public double[] normalizeFields(final Map<String, Double> values, final double defaultValue) {
        return this.normalizeNumbers(values, this.fields, defaultValue);
    }

    public double[] normalizeScores(final Map<String, Double> values, final Score.Valence valence, double defaultValue) {
        if(valence == Score.Valence.NEGATIVE) {
            return this.normalizeNumbers(values, this.negativeScores, defaultValue);
        }
        else if(valence == Score.Valence.POSITIVE) {
            return this.normalizeNumbers(values, this.positiveScores, defaultValue);
        }
        else {
            return this.normalizeNumbers(values, this.neutralScores, defaultValue);
        }
    }

    public double[] normalizeNumbers(
            final Map<String, Double> values,
            final Map<String, Integer> nameMap,
            final double defaultValue
        ) {
        final double[] numbers = new double[nameMap.size()];
        Arrays.fill(numbers, defaultValue);
        for(final Map.Entry<String, Double> value : values.entrySet()) {
            final Integer index = nameMap.get(value.getKey());
            if(index != null) {
                numbers[index] = value.getValue();
            }
        }
        return numbers;
    }

    public long[] normalizeTags(final Collection<String> tagNames) {
        final long[] bits = new long[this.getTagPageCount()];
        for(final String tagName : tagNames) {
            Integer tagIndex = this.tags.get(tagName);
            if(tagIndex == null) {
                tagIndex = this.tags.size();
            }
            bits[IndexMapper.getTagPage(tagIndex)] |= IndexMapper.getTagMask(tagIndex);
        }
        return bits;
    }

}
