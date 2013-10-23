package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;

import java.util.*;

public class IndexNormalizer {

    private final IndexMap indexMap;

    private final List<String> resultIds;
    private final List<long[]> tags;
    private final List<double[]> maxFields;
    private final List<double[]> minFields;
    private final List<double[]> scores;

    public IndexNormalizer(final IndexMap indexMap) {
        this.indexMap = indexMap;
        this.resultIds = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.minFields = new ArrayList<>();
        this.maxFields = new ArrayList<>();
        this.scores = new ArrayList<>();
    }

    public int getSize() {
        return this.resultIds.size();
    }

    public void index(final Document document) {
        this.indexResult(document);
    }

    private void indexResult(final Document document) {
        this.indexResultId(document);
        this.indexFields(document);
        this.indexScores(document);
        this.indexTags(document);
    }

    private void indexResultId(final Document document) {
        this.resultIds.add(document.getId());
    }

    private void indexFields(final Document item) {
        final Map<String, Double> fields = new HashMap<>();
        fields.putAll(item.getFieldValues());
        this.minFields.add(this.indexMap.normalizeFields(fields, Double.NEGATIVE_INFINITY));
        this.maxFields.add(this.indexMap.normalizeFields(fields, Double.POSITIVE_INFINITY));
    }

    private void indexScores(final Document item) {
        final Map<String, Double> scores = new HashMap<>();
        scores.putAll(item.getScoreValues());
        this.scores.add(this.indexMap.normalizeScores(scores, -1, true));
    }

    private void indexTags(final Document item) {
        final Set<String> tagNames = new HashSet<>();
        tagNames.addAll(item.getTagNames());
        this.tags.add(this.indexMap.normalizeTags(tagNames));
    }

    public IndexShard normalize() {
        final int size = this.resultIds.size();
        if(size > 0) {
            return new IndexShard(
                this.normalizeResultIds(),
                this.normalizeTags(),
                this.normalizeMinFields(),
                this.normalizeMaxFields(),
                this.normalizeScores()
            );
        }
        else {
            return null;
        }
    }

    public String[] normalizeResultIds() {
        return this.resultIds.toArray(new String[this.getSize()]);
    }

    public double[][] normalizeMaxFields() {
        return IndexNormalizer.transpose(this.maxFields.toArray(new double[this.getSize()][]));
    }

    public double[][] normalizeMinFields() {
        return IndexNormalizer.transpose(this.minFields.toArray(new double[this.getSize()][]));
    }

    public double[][] normalizeScores() {
        return this.scores.toArray(new double[this.getSize()][]);
    }

    public long[][] normalizeTags() {
        return IndexNormalizer.transpose(this.tags.toArray(new long[this.getSize()][]));
    }

    public static double[][] transpose(final double[][] source) {
        // big time copy/pasta, for primitive performance
        final double[][] sink = new double[source[0].length][source.length];
        for(int i = 0; i < source.length; i++) {
            final double[] current = source[i];
            for(int j = 0; j < current.length; j++) {
                sink[j][i] = current[j];
            }
        }
        return sink;
    }

    public static long[][] transpose(final long[][] source) {
        // big time copy/pasta, for primitive performance
        final long[][] sink = new long[source[0].length][source.length];
        for(int i = 0; i < source.length; i++) {
            final long[] current = source[i];
            for(int j = 0; j < current.length; j++) {
                sink[j][i] = current[j];
            }
        }
        return sink;
    }

}
