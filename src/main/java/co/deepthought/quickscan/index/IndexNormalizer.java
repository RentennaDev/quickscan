package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.HavingFields;
import co.deepthought.quickscan.store.Result;

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

    public void index(final Result result) {
        this.indexResult(result);
        for(final Document document : result.getDocuments()) {
            this.indexDocument(result, document);
        }
    }

    private void indexResult(final Result result) {
        this.indexResultId(result);
        this.indexFields(result);
        this.indexScores(false, result);
        this.indexTags(false, result);
    }

    private void indexDocument(final Result result, final Document document) {
        this.indexResultId(result);
        this.indexFields(result, document);
        this.indexScores(true, result, document);
        this.indexTags(true, result, document);
    }

    private void indexResultId(final Result result) {
        this.resultIds.add(result.getId());
    }

    private void indexFields(final HavingFields... items) {
        final Map<String, Double> fields = new HashMap<>();
        for(final HavingFields item : items) {
            fields.putAll(item.getFieldValues());
        }
        this.minFields.add(this.indexMap.normalizeFields(fields, Double.NEGATIVE_INFINITY));
        this.maxFields.add(this.indexMap.normalizeFields(fields, Double.POSITIVE_INFINITY));
    }

    private void indexScores(final boolean doc, final HavingFields... items) {
        final Map<String, Double> scores = new HashMap<>();
        if(doc) {
            scores.put("_doc", 1.0);
        }
        else {
            scores.put("_doc", 0.0);
        }
        for(final HavingFields item : items) {
            scores.putAll(item.getScoreValues());
        }
        this.scores.add(this.indexMap.normalizeScores(scores, -1, true));
    }

    private void indexTags(final boolean doc, final HavingFields... items) {
        final Set<String> tagNames = new HashSet<>();
        if(doc) {
            tagNames.add("_doc");
        }
        else {
            tagNames.add("_nodoc");
        }
        for(final HavingFields item : items) {
            tagNames.addAll(item.getTagNames());
        }
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
