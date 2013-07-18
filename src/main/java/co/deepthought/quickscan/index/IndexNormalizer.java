package co.deepthought.quickscan.index;

import co.deepthought.quickscan.store.Document;
import co.deepthought.quickscan.store.HavingFields;
import co.deepthought.quickscan.store.Result;

import java.util.*;

public class IndexNormalizer {

    private final IndexMap indexMap;

    private final List<String> resultIds;
    private final List<long[]> tags;
    private final List<double[]> fields;
    private final List<double[]> scores;

    public IndexNormalizer(final IndexMap indexMap) {
        this.indexMap = indexMap;
        this.resultIds = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.fields = new ArrayList<>();
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
        this.indexScores(result);
        this.indexTags("_nodoc", result);
    }

    private void indexDocument(final Result result, final Document document) {
        this.indexResultId(result);
        this.indexFields(result, document);
        this.indexScores(result, document);
        this.indexTags("_doc", result, document);
    }

    private void indexResultId(final Result result) {
        this.resultIds.add(result.getId());
    }

    private void indexFields(final HavingFields... items) {
        final Map<String, Double> fields = new HashMap<>();
        for(final HavingFields item : items) {
            fields.putAll(item.getFieldValues());
        }
        this.fields.add(this.indexMap.normalizeFields(fields, Double.NaN));
    }

    private void indexScores(final HavingFields... items) {
        final Map<String, Double> scores = new HashMap<>();
        for(final HavingFields item : items) {
            scores.putAll(item.getScoreValues());
        }
        this.scores.add(this.indexMap.normalizeScores(scores, -1, true));
    }

    private void indexTags(final String extraTag, final HavingFields... items) {
        final Set<String> tagNames = new HashSet<>();
        tagNames.add(extraTag);
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
                this.normalizeFields(),
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

    public double[][] normalizeFields() {
        return IndexNormalizer.transpose(this.fields.toArray(new double[this.getSize()][]));
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
